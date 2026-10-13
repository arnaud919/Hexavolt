package com.hexavolt.backend.service;

import java.util.List;

import com.hexavolt.backend.dto.MyReservationResponseDTO;
import com.hexavolt.backend.dto.OwnerReservationResponseDTO;
import com.hexavolt.backend.dto.ReservationCreateDTO;
import com.hexavolt.backend.entity.User;

public interface ReservationService {

    void createReservation(ReservationCreateDTO dto);

    List<MyReservationResponseDTO> getMyReservations(User connectedUser);

    void confirmReservation(Long reservationId, User owner);

    void rejectReservation(Long reservationId, User owner);

    List<OwnerReservationResponseDTO> getReservationsToProcess(User owner);

    void completeReservation(Long reservationId, User connectedUser);

    void markReservationAsAbsent(Long reservationId, User connectedUser);

}
