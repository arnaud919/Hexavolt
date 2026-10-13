package com.hexavolt.backend.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public class ReservationCreateDTO {

    @NotNull(message = "La borne est obligatoire.")
    private Long chargingStationId;

    @NotNull(message = "La date de début est obligatoire.")
    private LocalDateTime startDateTime;

    @NotNull(message = "La date de fin est obligatoire.")
    private LocalDateTime endDateTime;

    public Long getChargingStationId() {
        return chargingStationId;
    }

    public void setChargingStationId(Long chargingStationId) {
        this.chargingStationId = chargingStationId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}