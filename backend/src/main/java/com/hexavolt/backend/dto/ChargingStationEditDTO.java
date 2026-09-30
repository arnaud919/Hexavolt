package com.hexavolt.backend.dto;

import java.math.BigDecimal;

public class ChargingStationEditDTO {

    private Long id;
    private String name;
    private Long powerId;
    private BigDecimal hourlyRate;
    private String instruction;
    private Boolean isCustom;
    private Double latitude;
    private Double longitude;
    private Long statusId;

    public ChargingStationEditDTO(
            Long id,
            String name,
            Long powerId,
            BigDecimal hourlyRate,
            String instruction,
            Boolean isCustom,
            Double latitude,
            Double longitude,
            Long statusId
    ) {
        this.id = id;
        this.name = name;
        this.powerId = powerId;
        this.hourlyRate = hourlyRate;
        this.instruction = instruction;
        this.isCustom = isCustom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.statusId = statusId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getPowerId() {
        return powerId;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public String getInstruction() {
        return instruction;
    }

    public Boolean getIsCustom() {
        return isCustom;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Long getStatusId() {
        return statusId;
    }
}
