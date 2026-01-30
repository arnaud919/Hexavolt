package com.hexavolt.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "charging_station")
public class ChargingStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hourly_rate", precision = 4, scale = 2, nullable = false)
    private BigDecimal hourlyRate;

    @Column(name = "photo_name", length = 255)
    private String photoName;

    @Column(name = "video_name", length = 255)
    private String videoName;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String instruction;

    @ManyToOne(optional = false)
    @JoinColumn(name = "power", referencedColumnName = "id")
    private Power power;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location", referencedColumnName = "id")
    private StationLocation location;

    @Column(name = "is_custom", nullable = false)
    private Boolean isCustom;

    public ChargingStation() {
    }

    public ChargingStation(BigDecimal hourlyRate, String photoName, String videoName, Double latitude, Double longitude,
            String instruction, Power power, StationLocation location, Boolean isCustom) {
        this.hourlyRate = hourlyRate;
        this.photoName = photoName;
        this.videoName = videoName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.instruction = instruction;
        this.power = power;
        this.location = location;
        this.isCustom = isCustom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
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

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Power getPower() {
        return power;
    }

    public void setPower(Power power) {
        this.power = power;
    }

    public StationLocation getLocation() {
        return location;
    }

    public void setLocation(StationLocation location) {
        this.location = location;
    }

    public Boolean getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(Boolean isCustom) {
        this.isCustom = isCustom;
    }
}
