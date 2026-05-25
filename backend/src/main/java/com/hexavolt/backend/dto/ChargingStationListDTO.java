package com.hexavolt.backend.dto;

import java.math.BigDecimal;

public class ChargingStationListDTO {

    private Long id;
    private String name;
    private BigDecimal power;
    private BigDecimal hourlyRate;
    private boolean isCustom;
    private String locationName;
    private String statusName;
    private String photoUrl;

    public ChargingStationListDTO(Long id,
            String name,
            BigDecimal power,
            BigDecimal hourlyRate,
            boolean isCustom,
            String locationName,
            String statusName,
            String photoUrl) {
        this.id = id;
        this.name = name;
        this.power = power;
        this.hourlyRate = hourlyRate;
        this.isCustom = isCustom;
        this.locationName = locationName;
        this.statusName = statusName;
        this.photoUrl = photoUrl;
    }

    public ChargingStationListDTO(Long id, String name, BigDecimal power, BigDecimal hourlyRate, Boolean isCustom) {
        this.id = id;
        this.name = name;
        this.power = power;
        this.hourlyRate = hourlyRate;
        this.isCustom = isCustom;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getPower() {
        return power;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPower(BigDecimal power) {
        this.power = power;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public void setCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
