package com.hexavolt.backend.dto;

public class LocationDetailDTO {

    private Long locationId;
    private String nickname;
    private String address;
    private String postalCode;
    private Long cityId;
    private String cityName;

    public LocationDetailDTO(Long locationId,
                             String nickname,
                             String address,
                             String postalCode,
                             Long cityId,
                             String cityName) {
        this.locationId = locationId;
        this.nickname = nickname;
        this.address = address;
        this.postalCode = postalCode;
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public Long getLocationId() {
        return locationId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Long getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }
}
