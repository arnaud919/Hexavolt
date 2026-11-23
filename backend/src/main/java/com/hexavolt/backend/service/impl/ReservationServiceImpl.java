package com.hexavolt.backend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hexavolt.backend.dto.MyReservationResponseDTO;
import com.hexavolt.backend.dto.OwnerReservationResponseDTO;
import com.hexavolt.backend.dto.ReservationCreateDTO;
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
import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.service.ChargingStationAvailabilityService;
import com.hexavolt.backend.service.ReservationPricingService;
import com.hexavolt.backend.service.ReservationService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

        private static final String STATUS_PENDING = "EN_ATTENTE";
        private static final String STATUS_CONFIRMED = "CONFIRMEE";
        private static final String STATUS_REFUSED = "REFUSEE";
        private static final String STATUS_FINISHED = "TERMINEE";
        private static final String STATUS_ABSENCE = "ABSENCE";

        private final ReservationRepository reservationRepository;
        private final ChargingStationRepository chargingStationRepository;
        private final ChargingStationReservationRepository chargingStationReservationRepository;
        private final StatusReservationRepository statusReservationRepository;
        private final UserRepository userRepository;
        private final ReservationMapper reservationMapper;
        private final ReservationPricingService pricingService;
        private final ChargingStationAvailabilityService availabilityService;

        public ReservationServiceImpl(
                        ReservationRepository reservationRepository,
                        ChargingStationRepository chargingStationRepository,
                        ChargingStationReservationRepository chargingStationReservationRepository,
                        StatusReservationRepository statusReservationRepository,
                        UserRepository userRepository,
                        ReservationMapper reservationMapper,
                        ReservationPricingService pricingService,
                        ChargingStationAvailabilityService availabilityService) {
                this.reservationRepository = reservationRepository;
                this.chargingStationRepository = chargingStationRepository;
                this.chargingStationReservationRepository = chargingStationReservationRepository;
                this.statusReservationRepository = statusReservationRepository;
                this.userRepository = userRepository;
                this.reservationMapper = reservationMapper;
                this.pricingService = pricingService;
                this.availabilityService = availabilityService;
        }

        @Override
        public void createReservation(ReservationCreateDTO dto) {
                validateReservationCreateDTO(dto);

                User user = getCurrentUser();

                LocalDateTime start = dto.getStartDateTime();
                LocalDateTime end = dto.getEndDateTime();

                ChargingStation station = chargingStationRepository
                                .findById(dto.getChargingStationId())
                                .orElseThrow(() -> new ResourceNotFoundException("Borne introuvable."));

                ensureStationIsActive(station);
                ensureUserDoesNotOwnStation(station, user);

                if (!availabilityService.isAvailable(station, start, end)) {
                        throw new BusinessException(
                                        "Le créneau demandé ne correspond pas aux disponibilités de la borne.");
                }
                
                ensureNoBlockingConflict(station, start, end);

                StatusReservation pendingStatus = findStatusByName(STATUS_PENDING);

                Reservation reservation = new Reservation();
                reservation.setStartDateTime(start);
                reservation.setEndDateTime(end);
                reservation.setAmount(calculateAmount(station, start, end));
                reservation.setReceipt(null);
                reservation.setUser(user);
                reservation.setStatus(pendingStatus);
                reservation.setCreatedAt(LocalDateTime.now());

                reservationRepository.save(reservation);

                ChargingStationReservation link = new ChargingStationReservation();
                link.setChargingStation(station);
                link.setReservation(reservation);

                chargingStationReservationRepository.save(link);
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
        public void rejectReservation(Long reservationId, User connectedUser) {
                ChargingStationReservation csr = findChargingStationReservation(reservationId);

                ChargingStation station = csr.getChargingStation();
                Reservation reservation = csr.getReservation();

                ensureOwner(station, connectedUser);
                ensureStatus(reservation, STATUS_PENDING, "La réservation n'est pas en attente.");

                StatusReservation refused = findStatusByName(STATUS_REFUSED);

                reservation.setStatus(refused);
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

        private void validateReservationCreateDTO(ReservationCreateDTO dto) {
                if (dto == null) {
                        throw new BusinessException("Les informations de réservation sont obligatoires.");
                }

                if (dto.getChargingStationId() == null) {
                        throw new BusinessException("La borne est obligatoire.");
                }

                if (dto.getStartDateTime() == null) {
                        throw new BusinessException("La date de début est obligatoire.");
                }

                if (dto.getEndDateTime() == null) {
                        throw new BusinessException("La date de fin est obligatoire.");
                }

                if (!dto.getStartDateTime().isAfter(LocalDateTime.now())) {
                        throw new BusinessException("La date de début doit être dans le futur.");
                }

                if (!dto.getEndDateTime().isAfter(dto.getStartDateTime())) {
                        throw new BusinessException("La date de fin doit être après la date de début.");
                }
        }

        private static final String STATUS_STATION_ACTIVE = "ACTIVE";

        private void ensureStationIsActive(ChargingStation station) {
                if (station.getStatus() == null || !STATUS_STATION_ACTIVE.equals(station.getStatus().getName())) {
                        throw new BusinessException("Cette borne n'est pas disponible à la réservation.");
                }
        }

        private void ensureUserDoesNotOwnStation(ChargingStation station, User user) {
                User owner = station.getLocation().getUser();

                if (owner != null && owner.getId().equals(user.getId())) {
                        throw new BusinessException("Vous ne pouvez pas réserver votre propre borne.");
                }
        }

        private void ensureNoBlockingConflict(
                        ChargingStation station,
                        LocalDateTime start,
                        LocalDateTime end) {
                long conflicts = chargingStationReservationRepository.countBlockingReservationConflict(
                                station.getId(),
                                start,
                                end);

                if (conflicts > 0) {
                        throw new BusinessException("Ce créneau est déjà demandé ou réservé.");
                }
        }

        private BigDecimal calculateAmount(
                        ChargingStation station,
                        LocalDateTime start,
                        LocalDateTime end) {
                long minutes = Duration.between(start, end).toMinutes();

                BigDecimal hours = BigDecimal.valueOf(minutes)
                                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

                BigDecimal amount = station.getHourlyRate()
                                .multiply(hours)
                                .setScale(2, RoundingMode.HALF_UP);

                if (amount.compareTo(new BigDecimal("999.99")) > 0) {
                        throw new BusinessException("Le montant de la réservation est trop élevé.");
                }

                return amount;
        }

        private User getCurrentUser() {
                String email = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();

                return userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));
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