package com.hexavolt.backend.dto;

public class LocationListDTO {
    private Long locationId;
    private String nickname;
    private String address;
    private String postalCode;
    private String cityName;

    public LocationListDTO(
            Long locationId,
            String nickname,
            String address,
            String postalCode,
            String cityName) {
        this.locationId = locationId;
        this.nickname = nickname;
        this.address = address;
        this.postalCode = postalCode;
        this.cityName = cityName;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
