package com.hexavolt.backend.dto;

import java.time.LocalDateTime;

public class OwnerReservationResponseDTO {

    private Long reservationId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String status;

    private Long chargingStationId;
    private String chargingStationName;

    private Long reservingUserId;
    private String reservingUserEmail;
    
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
    public Long getReservingUserId() {
        return reservingUserId;
    }
    public void setReservingUserId(Long reservingUserId) {
        this.reservingUserId = reservingUserId;
    }
    public String getReservingUserEmail() {
        return reservingUserEmail;
    }
    public void setReservingUserEmail(String reservingUserEmail) {
        this.reservingUserEmail = reservingUserEmail;
    }

}
