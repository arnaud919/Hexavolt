package com.hexavolt.backend.dto;

public class AddressReverseGeocodeResultDTO {

    private String address;
    private String postalCode;
    private String cityName;
    private String label;
    private String type;
    private String cityCode;
    private Long cityId;

    public AddressReverseGeocodeResultDTO(
            String address,
            String postalCode,
            String cityName,
            String label,
            String type,
            String cityCode) {

        this.address = address;
        this.postalCode = postalCode;
        this.cityName = cityName;
        this.label = label;
        this.type = type;
        this.cityCode = cityCode;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCityName() {
        return cityName;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public String getCityCode() {
        return cityCode;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
}