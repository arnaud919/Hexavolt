package com.hexavolt.backend.dto;

import java.math.BigDecimal;

public class ChargingStationSearchDTO {

    private Long id;
    private String name;
    private BigDecimal power;
    private BigDecimal hourlyRate;
    private String cityName;
    private String locationAddress;
    private String photoUrl;
    private Double latitude;
    private Double longitude;

    public ChargingStationSearchDTO(
            Long id,
            String name,
            BigDecimal power,
            BigDecimal hourlyRate,
            String cityName,
            String locationAddress,
            String photoUrl,
            Double latitude,
            Double longitude) {
        this.id = id;
        this.name = name;
        this.power = power;
        this.hourlyRate = hourlyRate;
        this.cityName = cityName;
        this.locationAddress = locationAddress;
        this.photoUrl = photoUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPower() {
        return power;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public String getCityName() {
        return cityName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
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

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}