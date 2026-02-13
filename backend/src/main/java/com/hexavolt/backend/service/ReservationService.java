package com.hexavolt.backend.service;

import java.util.List;

import com.hexavolt.backend.dto.MyReservationResponseDTO;
import com.hexavolt.backend.dto.OwnerReservationResponseDTO;
import com.hexavolt.backend.dto.ReservationCreateRequestDTO;
import com.hexavolt.backend.dto.ReservationResponseDTO;
import com.hexavolt.backend.entity.User;

public interface ReservationService {

    ReservationResponseDTO createReservation(
            ReservationCreateRequestDTO request,
            User connectedUser);

    List<MyReservationResponseDTO> getMyReservations(User connectedUser);

    void confirmReservation(Long reservationId, User owner);

    void rejectReservation(Long reservationId, User owner);

    List<OwnerReservationResponseDTO> getReservationsToProcess(User owner);

    void completeReservation(Long reservationId, User connectedUser);

    void markReservationAsAbsent(Long reservationId, User connectedUser);

}
