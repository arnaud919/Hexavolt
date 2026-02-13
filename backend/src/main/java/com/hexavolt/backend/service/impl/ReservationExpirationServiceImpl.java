package com.hexavolt.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hexavolt.backend.entity.Reservation;
import com.hexavolt.backend.entity.StatusReservation;
import com.hexavolt.backend.repository.ReservationRepository;
import com.hexavolt.backend.repository.StatusReservationRepository;
import com.hexavolt.backend.service.ReservationExpirationService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReservationExpirationServiceImpl
        implements ReservationExpirationService {

    private final ReservationRepository reservationRepository;
    private final StatusReservationRepository statusRepository;

    public ReservationExpirationServiceImpl(
            ReservationRepository reservationRepository,
            StatusReservationRepository statusRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.statusRepository = statusRepository;
    }

    @Override
    public void expirePendingReservations() {

        StatusReservation expired =
                statusRepository.findByName("EXPIREE").orElseThrow();

        List<Reservation> reservations =
                reservationRepository.findByStatusNameAndStartDateTimeBefore(
                        "EN_ATTENTE",
                        LocalDateTime.now()
                );

        reservations.forEach(r -> r.setStatus(expired));
    }
}
