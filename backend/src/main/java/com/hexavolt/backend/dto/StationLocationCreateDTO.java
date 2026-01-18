package com.hexavolt.backend.dto;

public class StationLocationCreateDTO {
    private String nickname;
    private String address;
    private String postalCode;
    private Integer cityId;

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
    public Integer getCityId() {
        return cityId;
    }
    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }
}
