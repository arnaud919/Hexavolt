package com.hexavolt.backend.dto;

import java.time.LocalDateTime;

public class ReservationCreateRequestDTO {

    private Long chargingStationId;
    private LocalDateTime startDateTime;
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

    // getters / setters
}

