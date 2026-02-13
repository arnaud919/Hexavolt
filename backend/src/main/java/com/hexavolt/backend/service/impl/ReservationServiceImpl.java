package com.hexavolt.backend.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hexavolt.backend.dto.MyReservationResponseDTO;
import com.hexavolt.backend.dto.OwnerReservationResponseDTO;
import com.hexavolt.backend.dto.ReservationCreateRequestDTO;
import com.hexavolt.backend.dto.ReservationResponseDTO;
import com.hexavolt.backend.entity.ChargingStation;
import com.hexavolt.backend.entity.ChargingStationReservation;
import com.hexavolt.backend.entity.Reservation;
import com.hexavolt.backend.entity.StatusReservation;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.mapper.ReservationMapper;
import com.hexavolt.backend.repository.ChargingStationRepository;
import com.hexavolt.backend.repository.ChargingStationReservationRepository;
import com.hexavolt.backend.repository.ReservationRepository;
import com.hexavolt.backend.repository.StatusReservationRepository;
import com.hexavolt.backend.service.ChargingStationAvailabilityService;
import com.hexavolt.backend.service.ReservationPricingService;
import com.hexavolt.backend.service.ReservationService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

        private final ReservationRepository reservationRepository;
        private final ChargingStationRepository chargingStationRepository;
        private final ChargingStationReservationRepository chargingStationReservationRepository;
        private final StatusReservationRepository statusReservationRepository;
        private final ReservationMapper reservationMapper;
        private final ReservationPricingService pricingService;
        private final ChargingStationAvailabilityService availabilityService;

        public ReservationServiceImpl(
                        ReservationRepository reservationRepository,
                        ChargingStationRepository chargingStationRepository,
                        ChargingStationReservationRepository chargingStationReservationRepository,
                        StatusReservationRepository statusReservationRepository,
                        ReservationMapper reservationMapper,
                        ReservationPricingService pricingService,
                        ChargingStationAvailabilityService availabilityService) {
                this.reservationRepository = reservationRepository;
                this.chargingStationRepository = chargingStationRepository;
                this.chargingStationReservationRepository = chargingStationReservationRepository;
                this.statusReservationRepository = statusReservationRepository;
                this.reservationMapper = reservationMapper;
                this.pricingService = pricingService;
                this.availabilityService = availabilityService;
        }

        @Override
        public ReservationResponseDTO createReservation(
                        ReservationCreateRequestDTO request,
                        User connectedUser) {

                // 1️⃣ Validation des dates
                if (request.getStartDateTime().isAfter(request.getEndDateTime())
                                || request.getStartDateTime().isEqual(request.getEndDateTime())) {
                        throw new IllegalArgumentException(
                                        "La date de début doit être antérieure à la date de fin");
                }

                if (request.getStartDateTime().isBefore(LocalDateTime.now())) {
                        throw new IllegalArgumentException(
                                        "La date de début doit être dans le futur");
                }

                // 2️⃣ Récupération de la borne
                ChargingStation chargingStation = chargingStationRepository
                                .findById(request.getChargingStationId())
                                .orElseThrow(() -> new IllegalArgumentException("Borne introuvable"));

                // 3️⃣ Vérification de la mise à disposition (horaires complets)
                boolean available = availabilityService.isAvailable(
                                chargingStation,
                                request.getStartDateTime(),
                                request.getEndDateTime());

                if (!available) {
                        throw new IllegalStateException(
                                        "La borne n'est pas disponible sur ce créneau");
                }

                // 4️⃣ Vérification des conflits horaires (réservations CONFIRMEE)
                long conflicts = chargingStationReservationRepository
                                .countConfirmedReservationConflict(
                                                chargingStation.getId(),
                                                request.getStartDateTime(),
                                                request.getEndDateTime());

                if (conflicts > 0) {
                        throw new IllegalStateException(
                                        "Ce créneau est déjà réservé pour cette borne");
                }

                // 5️⃣ Récupération du statut EN_ATTENTE
                StatusReservation statusEnAttente = statusReservationRepository
                                .findByName("EN_ATTENTE")
                                .orElseThrow(() -> new IllegalStateException(
                                                "Statut EN_ATTENTE introuvable"));

                // 6️⃣ Création de la réservation
                Reservation reservation = new Reservation();
                reservation.setStartDateTime(request.getStartDateTime());
                reservation.setEndDateTime(request.getEndDateTime());
                reservation.setAmount(BigDecimal.ZERO);
                reservation.setReceipt(null);
                reservation.setUser(connectedUser);
                reservation.setStatus(statusEnAttente);

                reservationRepository.save(reservation);

                // 7️⃣ Association borne ↔ réservation
                ChargingStationReservation csr = new ChargingStationReservation();
                csr.setChargingStation(chargingStation);
                csr.setReservation(reservation);

                chargingStationReservationRepository.save(csr);

                // 8️⃣ Mapping vers DTO de sortie
                return reservationMapper.toResponseDTO(reservation, chargingStation);
        }

        public List<MyReservationResponseDTO> getMyReservations(User connectedUser) {
                return chargingStationReservationRepository
                                .findByReservationUserIdOrderByReservationStartDateTimeDesc(connectedUser.getId())
                                .stream()
                                .map(reservationMapper::toMyReservationDTO)
                                .toList();
        }

        @Transactional
        public void confirmReservation(Long reservationId, User connectedUser) {

                ChargingStationReservation csr = chargingStationReservationRepository.findByReservationId(reservationId)
                                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));

                ChargingStation station = csr.getChargingStation();
                Reservation reservation = csr.getReservation();

                // 🔐 1) Vérifier que l'utilisateur connecté est le propriétaire du lieu
                User owner = station.getLocation().getUser();

                if (!owner.getId().equals(connectedUser.getId())) {
                        throw new IllegalStateException("Action non autorisée");
                }

                // 🕒 2) Vérifier le statut
                if (!"EN_ATTENTE".equals(reservation.getStatus().getName())) {
                        throw new IllegalStateException("La réservation n'est pas en attente");
                }

                // ⚠️ 3) Vérifier les conflits horaires (réservations CONFIRMEE uniquement)
                long conflicts = chargingStationReservationRepository.countConfirmedReservationConflict(
                                station.getId(),
                                reservation.getStartDateTime(),
                                reservation.getEndDateTime());

                if (conflicts > 0) {
                        throw new IllegalStateException("Conflit horaire détecté");
                }

                // ✅ 4) Passer à CONFIRMEE
                StatusReservation confirmed = statusReservationRepository.findByName("CONFIRMEE")
                                .orElseThrow(() -> new IllegalStateException("Statut CONFIRMEE introuvable"));

                reservation.setStatus(confirmed);
        }

        @Transactional
        public void rejectReservation(Long reservationId, User connectedUser) {

                ChargingStationReservation csr = chargingStationReservationRepository.findByReservationId(reservationId)
                                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));

                ChargingStation station = csr.getChargingStation();
                Reservation reservation = csr.getReservation();

                User owner = station.getLocation().getUser();

                if (!owner.getId().equals(connectedUser.getId())) {
                        throw new IllegalStateException("Action non autorisée");
                }

                if (!"EN_ATTENTE".equals(reservation.getStatus().getName())) {
                        throw new IllegalStateException("La réservation n'est pas en attente");
                }

                StatusReservation cancelled = statusReservationRepository.findByName("ANNULEE")
                                .orElseThrow(() -> new IllegalStateException("Statut ANNULEE introuvable"));

                reservation.setStatus(cancelled);
        }

        public List<OwnerReservationResponseDTO> getReservationsToProcess(User owner) {

                return chargingStationReservationRepository
                                .findByChargingStationLocationUserIdAndReservationStatusNameOrderByReservationStartDateTimeAsc(
                                                owner.getId(),
                                                "EN_ATTENTE")
                                .stream()
                                .map(reservationMapper::toOwnerReservationDTO)
                                .toList();
        }

        @Transactional
        public void completeReservation(Long reservationId, User connectedUser) {

                ChargingStationReservation csr = chargingStationReservationRepository.findByReservationId(reservationId)
                                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));

                ChargingStation station = csr.getChargingStation();
                Reservation reservation = csr.getReservation();

                // 🔐 Propriétaire réel
                User owner = station.getLocation().getUser();
                if (!owner.getId().equals(connectedUser.getId())) {
                        throw new IllegalStateException("Action non autorisée");
                }

                // 🕒 Statut
                if (!"CONFIRMEE".equals(reservation.getStatus().getName())) {
                        throw new IllegalStateException("La réservation n'est pas confirmée");
                }

                // ⏱️ Fin atteinte
                if (reservation.getEndDateTime().isAfter(LocalDateTime.now())) {
                        throw new IllegalStateException("La réservation n'est pas encore terminée");
                }

                StatusReservation finished = statusReservationRepository.findByName("TERMINEE")
                                .orElseThrow(() -> new IllegalStateException("Statut TERMINEE introuvable"));

                BigDecimal amount = pricingService.calculateAmount(reservation);

                reservation.setAmount(amount);
                reservation.setStatus(finished);

        }

        @Transactional
        public void markReservationAsAbsent(Long reservationId, User connectedUser) {

                ChargingStationReservation csr = chargingStationReservationRepository.findByReservationId(reservationId)
                                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));

                ChargingStation station = csr.getChargingStation();
                Reservation reservation = csr.getReservation();

                User owner = station.getLocation().getUser();
                if (!owner.getId().equals(connectedUser.getId())) {
                        throw new IllegalStateException("Action non autorisée");
                }

                if (!"CONFIRMEE".equals(reservation.getStatus().getName())) {
                        throw new IllegalStateException("La réservation n'est pas confirmée");
                }

                if (reservation.getEndDateTime().isAfter(LocalDateTime.now())) {
                        throw new IllegalStateException("La réservation n'est pas encore terminée");
                }

                StatusReservation absence = statusReservationRepository.findByName("ABSENCE")
                                .orElseThrow(() -> new IllegalStateException("Statut ABSENCE introuvable"));

                reservation.setStatus(absence);
        }

}
