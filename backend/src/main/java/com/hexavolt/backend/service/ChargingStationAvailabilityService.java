package com.hexavolt.backend.service;

import java.time.LocalDateTime;

import com.hexavolt.backend.entity.ChargingStation;

public interface ChargingStationAvailabilityService {

    boolean isAvailable(
            ChargingStation station,
            LocalDateTime start,
            LocalDateTime end
    );

    boolean isWithinWeeklySchedule(
        Long stationId,
        LocalDateTime start,
        LocalDateTime end);
}

