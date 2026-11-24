
package com.hexavolt.backend.service;

import java.time.LocalDate;
import java.util.List;

import com.hexavolt.backend.dto.AvailableSlotDTO;

public interface ChargingStationAvailableSlotService {

    List<AvailableSlotDTO> getAvailableSlots(
            Long stationId,
            LocalDate date);
}

