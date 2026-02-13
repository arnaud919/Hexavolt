package com.hexavolt.backend.mapper;

import com.hexavolt.backend.dto.MyReservationResponseDTO;
import com.hexavolt.backend.dto.OwnerReservationResponseDTO;
import com.hexavolt.backend.dto.ReservationResponseDTO;
import com.hexavolt.backend.entity.ChargingStation;
import com.hexavolt.backend.entity.ChargingStationReservation;
import com.hexavolt.backend.entity.Reservation;
import com.hexavolt.backend.entity.User;

import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponseDTO toResponseDTO(
            Reservation reservation,
            ChargingStation chargingStation) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setStartDateTime(reservation.getStartDateTime());
        dto.setEndDateTime(reservation.getEndDateTime());
        dto.setAmount(reservation.getAmount());
        dto.setStatus(reservation.getStatus().getName());
        dto.setChargingStationId(chargingStation.getId());

        return dto;
    }

    public MyReservationResponseDTO toMyReservationDTO(
            ChargingStationReservation csr) {
        Reservation r = csr.getReservation();
        ChargingStation cs = csr.getChargingStation();

        MyReservationResponseDTO dto = new MyReservationResponseDTO();
        dto.setReservationId(r.getId());
        dto.setStartDateTime(r.getStartDateTime());
        dto.setEndDateTime(r.getEndDateTime());
        dto.setAmount(r.getAmount());
        dto.setStatus(r.getStatus().getName());
        dto.setChargingStationId(cs.getId());
        dto.setChargingStationName(cs.getName());

        return dto;
    }

    public OwnerReservationResponseDTO toOwnerReservationDTO(
            ChargingStationReservation csr) {
        Reservation r = csr.getReservation();
        ChargingStation cs = csr.getChargingStation();
        User reservingUser = r.getUser();

        OwnerReservationResponseDTO dto = new OwnerReservationResponseDTO();
        dto.setReservationId(r.getId());
        dto.setStartDateTime(r.getStartDateTime());
        dto.setEndDateTime(r.getEndDateTime());
        dto.setStatus(r.getStatus().getName());

        dto.setChargingStationId(cs.getId());
        dto.setChargingStationName(cs.getName());

        dto.setReservingUserId(reservingUser.getId());
        dto.setReservingUserEmail(reservingUser.getEmail());

        return dto;
    }

}
