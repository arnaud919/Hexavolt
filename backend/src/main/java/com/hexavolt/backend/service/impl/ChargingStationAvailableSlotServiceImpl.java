
package com.hexavolt.backend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hexavolt.backend.dto.AvailableSlotDTO;
import com.hexavolt.backend.entity.ChargingStation;
import com.hexavolt.backend.exception.BusinessException;
import com.hexavolt.backend.exception.ResourceNotFoundException;
import com.hexavolt.backend.repository.ChargingStationRepository;
import com.hexavolt.backend.repository.ChargingStationReservationRepository;
import com.hexavolt.backend.service.ChargingStationAvailabilityService;
import com.hexavolt.backend.service.ChargingStationAvailableSlotService;

@Service
@Transactional(readOnly = true)
public class ChargingStationAvailableSlotServiceImpl
        implements ChargingStationAvailableSlotService {

    private final ChargingStationRepository stationRepo;
    private final ChargingStationReservationRepository reservationRepo;
    private final ChargingStationAvailabilityService availabilityService;

    public ChargingStationAvailableSlotServiceImpl(
            ChargingStationRepository stationRepo,
            ChargingStationReservationRepository reservationRepo,
            ChargingStationAvailabilityService availabilityService) {

        this.stationRepo = stationRepo;
        this.reservationRepo = reservationRepo;
        this.availabilityService = availabilityService;
    }

    @Override
    public List<AvailableSlotDTO> getAvailableSlots(
            Long stationId,
            LocalDate date) {

        if (stationId == null || stationId <= 0 || date == null) {
            throw new BusinessException(
                    "La borne et la date sont obligatoires.");
        }

        ChargingStation station = stationRepo
                .findByIdAndStatusName(stationId, "ACTIVE")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Borne introuvable ou indisponible."));

        List<AvailableSlotDTO> slots = new ArrayList<>();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime nextDay = date.plusDays(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        while (start.plusMinutes(30).isBefore(nextDay)) {
            LocalDateTime end = start.plusMinutes(30);

            if (start.isAfter(now)
                    && availabilityService.isAvailable(station, start, end)
                    && reservationRepo.countBlockingReservationConflict(
                            station.getId(), start, end) == 0) {

                slots.add(new AvailableSlotDTO(
                        start.toLocalTime(),
                        end.toLocalTime()));
            }

            start = end;
        }

        return slots;
    }
}
