package com.hexavolt.backend.dto;

import java.math.BigDecimal;

public class ChargingStationDetailDTO {

    private Long id;
    private String name;
    private BigDecimal power;
    private BigDecimal hourlyRate;
    private String instruction;
    private Double latitude;
    private Double longitude;
    private Boolean isCustom;
    private String statusName;
    private String locationAddress;
    private String cityName;
    private String photoName;
    private String videoName;

    public ChargingStationDetailDTO(
            Long id,
            String name,
            BigDecimal power,
            BigDecimal hourlyRate,
            String instruction,
            Double latitude,
            Double longitude,
            Boolean isCustom,
            String statusName,
            String locationAddress,
            String cityName,
            String photoName,
            String videoName
    ) {
        this.id = id;
        this.name = name;
        this.power = power;
        this.hourlyRate = hourlyRate;
        this.instruction = instruction;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isCustom = isCustom;
        this.statusName = statusName;
        this.locationAddress = locationAddress;
        this.cityName = cityName;
        this.photoName = photoName;
        this.videoName = videoName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPower() {
        return power;
    }

    public void setPower(BigDecimal power) {
        this.power = power;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(Boolean isCustom) {
        this.isCustom = isCustom;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    // getters
}