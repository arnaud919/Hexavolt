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
    private String photoUrl;
    private String videoUrl;
    private String locationName;

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
            String photoUrl,
            String videoUrl,
            String locationName) {
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
        this.photoUrl = photoUrl;
        this.videoUrl = videoUrl;
        this.locationName = locationName;
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

    public String getLocationName() {
        return locationName;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}