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
import com.hexavolt.backend.exception.BusinessException;
import com.hexavolt.backend.exception.ForbiddenActionException;
import com.hexavolt.backend.exception.ResourceNotFoundException;
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

        private static final String STATUS_PENDING = "EN_ATTENTE";
        private static final String STATUS_CONFIRMED = "CONFIRMEE";
        private static final String STATUS_CANCELLED = "ANNULEE";
        private static final String STATUS_FINISHED = "TERMINEE";
        private static final String STATUS_ABSENCE = "ABSENCE";

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

                validateReservationRequest(request);

                ChargingStation chargingStation = chargingStationRepository
                                .findById(request.getChargingStationId())
                                .orElseThrow(() -> new ResourceNotFoundException("Borne introuvable."));

                boolean available = availabilityService.isAvailable(
                                chargingStation,
                                request.getStartDateTime(),
                                request.getEndDateTime());

                if (!available) {
                        throw new BusinessException("La borne n'est pas disponible sur ce créneau.");
                }

                long conflicts = chargingStationReservationRepository
                                .countConfirmedReservationConflict(
                                                chargingStation.getId(),
                                                request.getStartDateTime(),
                                                request.getEndDateTime());

                if (conflicts > 0) {
                        throw new BusinessException("Ce créneau est déjà réservé pour cette borne.");
                }

                StatusReservation statusEnAttente = findStatusByName(STATUS_PENDING);

                Reservation reservation = new Reservation();
                reservation.setStartDateTime(request.getStartDateTime());
                reservation.setEndDateTime(request.getEndDateTime());
                reservation.setAmount(BigDecimal.ZERO);
                reservation.setReceipt(null);
                reservation.setUser(connectedUser);
                reservation.setStatus(statusEnAttente);

                reservationRepository.save(reservation);

                ChargingStationReservation csr = new ChargingStationReservation();
                csr.setChargingStation(chargingStation);
                csr.setReservation(reservation);

                chargingStationReservationRepository.save(csr);

                return reservationMapper.toResponseDTO(reservation, chargingStation);
        }

        @Override
        public List<MyReservationResponseDTO> getMyReservations(User connectedUser) {
                return chargingStationReservationRepository
                                .findByReservationUserIdOrderByReservationStartDateTimeDesc(connectedUser.getId())
                                .stream()
                                .map(reservationMapper::toMyReservationDTO)
                                .toList();
        }

        @Override
        @Transactional
        public void confirmReservation(Long reservationId, User connectedUser) {
                ChargingStationReservation csr = findChargingStationReservation(reservationId);

                ChargingStation station = csr.getChargingStation();
                Reservation reservation = csr.getReservation();

                ensureOwner(station, connectedUser);
                ensureStatus(reservation, STATUS_PENDING, "La réservation n'est pas en attente.");

                long conflicts = chargingStationReservationRepository.countConfirmedReservationConflict(
                                station.getId(),
                                reservation.getStartDateTime(),
                                reservation.getEndDateTime());

                if (conflicts > 0) {
                        throw new BusinessException("Conflit horaire détecté.");
                }

                StatusReservation confirmed = findStatusByName(STATUS_CONFIRMED);

                reservation.setStatus(confirmed);
        }

        @Override
        @Transactional
        public void rejectReservation(Long reservationId, User connectedUser) {
                ChargingStationReservation csr = findChargingStationReservation(reservationId);

                ChargingStation station = csr.getChargingStation();
                Reservation reservation = csr.getReservation();

                ensureOwner(station, connectedUser);
                ensureStatus(reservation, STATUS_PENDING, "La réservation n'est pas en attente.");

                StatusReservation cancelled = findStatusByName(STATUS_CANCELLED);

                reservation.setStatus(cancelled);
        }

        @Override
        public List<OwnerReservationResponseDTO> getReservationsToProcess(User owner) {
                return chargingStationReservationRepository
                                .findByChargingStationLocationUserIdAndReservationStatusNameOrderByReservationStartDateTimeAsc(
                                                owner.getId(),
                                                STATUS_PENDING)
                                .stream()
                                .map(reservationMapper::toOwnerReservationDTO)
                                .toList();
        }

        @Override
        @Transactional
        public void completeReservation(Long reservationId, User connectedUser) {
                ChargingStationReservation csr = findChargingStationReservation(reservationId);

                ChargingStation station = csr.getChargingStation();
                Reservation reservation = csr.getReservation();

                ensureOwner(station, connectedUser);
                ensureStatus(reservation, STATUS_CONFIRMED, "La réservation n'est pas confirmée.");
                ensureReservationEnded(reservation);

                StatusReservation finished = findStatusByName(STATUS_FINISHED);

                BigDecimal amount = pricingService.calculateAmount(reservation);

                reservation.setAmount(amount);
                reservation.setStatus(finished);
        }

        @Override
        @Transactional
        public void markReservationAsAbsent(Long reservationId, User connectedUser) {
                ChargingStationReservation csr = findChargingStationReservation(reservationId);

                ChargingStation station = csr.getChargingStation();
                Reservation reservation = csr.getReservation();

                ensureOwner(station, connectedUser);
                ensureStatus(reservation, STATUS_CONFIRMED, "La réservation n'est pas confirmée.");
                ensureReservationEnded(reservation);

                StatusReservation absence = findStatusByName(STATUS_ABSENCE);

                reservation.setStatus(absence);
        }

        private void validateReservationRequest(ReservationCreateRequestDTO request) {
                if (request == null) {
                        throw new BusinessException("Les informations de réservation sont obligatoires.");
                }

                if (request.getChargingStationId() == null) {
                        throw new BusinessException("La borne est obligatoire.");
                }

                if (request.getStartDateTime() == null || request.getEndDateTime() == null) {
                        throw new BusinessException("Les dates de début et de fin sont obligatoires.");
                }

                if (!request.getStartDateTime().isBefore(request.getEndDateTime())) {
                        throw new BusinessException("La date de début doit être antérieure à la date de fin.");
                }

                if (request.getStartDateTime().isBefore(LocalDateTime.now())) {
                        throw new BusinessException("La date de début doit être dans le futur.");
                }
        }

        private ChargingStationReservation findChargingStationReservation(Long reservationId) {
                if (reservationId == null) {
                        throw new BusinessException("L'identifiant de réservation est obligatoire.");
                }

                return chargingStationReservationRepository.findByReservationId(reservationId)
                                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable."));
        }

        private StatusReservation findStatusByName(String statusName) {
                return statusReservationRepository.findByName(statusName)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Statut de réservation introuvable : " + statusName + "."));
        }

        private void ensureOwner(ChargingStation station, User connectedUser) {
                User owner = station.getLocation().getUser();

                if (!owner.getId().equals(connectedUser.getId())) {
                        throw new ForbiddenActionException("Action non autorisée.");
                }
        }

        private void ensureStatus(
                        Reservation reservation,
                        String expectedStatus,
                        String errorMessage) {

                if (reservation.getStatus() == null
                                || !expectedStatus.equals(reservation.getStatus().getName())) {
                        throw new BusinessException(errorMessage);
                }
        }

        private void ensureReservationEnded(Reservation reservation) {
                if (reservation.getEndDateTime().isAfter(LocalDateTime.now())) {
                        throw new BusinessException("La réservation n'est pas encore terminée.");
                }
        }
}