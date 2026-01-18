package com.hexavolt.backend.dto;

public class LocationDetailDTO {

    private Integer locationId;
    private String nickname;
    private String address;
    private String postalCode;
    private String cityName;

    public LocationDetailDTO(Integer locationId,
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

    public Integer getLocationId() { return locationId; }
    public String getNickname() { return nickname; }
    public String getAddress() { return address; }
    public String getPostalCode() { return postalCode; }
    public String getCityName() { return cityName; }
}

