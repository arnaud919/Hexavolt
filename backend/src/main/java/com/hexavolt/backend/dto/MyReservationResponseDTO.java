package com.hexavolt.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MyReservationResponseDTO {

    private Long reservationId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String status;
    private BigDecimal amount;

    private Long chargingStationId;
    private String chargingStationName;
    
    public Long getReservationId() {
        return reservationId;
    }
    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
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
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public Long getChargingStationId() {
        return chargingStationId;
    }
    public void setChargingStationId(Long chargingStationId) {
        this.chargingStationId = chargingStationId;
    }
    public String getChargingStationName() {
        return chargingStationName;
    }
    public void setChargingStationName(String chargingStationName) {
        this.chargingStationName = chargingStationName;
    }


}
