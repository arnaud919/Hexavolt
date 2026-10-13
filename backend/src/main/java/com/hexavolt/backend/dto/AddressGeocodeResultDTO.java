package com.hexavolt.backend.dto;

public class AddressGeocodeResultDTO {

    private String label;
    private Double latitude;
    private Double longitude;
    private Double score;

    public AddressGeocodeResultDTO(String label, Double latitude, Double longitude, Double score) {
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
        this.score = score;
    }

    public String getLabel() {
        return label;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getScore() {
        return score;
    }
}