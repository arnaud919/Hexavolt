package com.hexavolt.backend.dto;

public class LocationDetailDTO {

    private Long locationId;
    private String nickname;
    private String address;
    private String postalCode;
    private Long cityId;
    private String cityName;
    private Double latitude;
    private Double longitude;

    public LocationDetailDTO(Long locationId,
            String nickname,
            String address,
            String postalCode,
            Long cityId,
            String cityName,
            Double latitude,
            Double longitude) {
        this.locationId = locationId;
        this.nickname = nickname;
        this.address = address;
        this.postalCode = postalCode;
        this.cityId = cityId;
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
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
}
