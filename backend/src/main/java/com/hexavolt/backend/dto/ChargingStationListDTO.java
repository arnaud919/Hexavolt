package com.hexavolt.backend.dto;

import java.math.BigDecimal;

public class ChargingStationListDTO {

    private Long id;
    private BigDecimal power;
    private BigDecimal hourlyRate;
    private boolean isCustom;

    public ChargingStationListDTO(Long id,
            BigDecimal power,
            BigDecimal hourlyRate,
            boolean isCustom) {
        this.id = id;
        this.power = power;
        this.hourlyRate = hourlyRate;
        this.isCustom = isCustom;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getPower() {
        return power;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public boolean isCustom() {
        return isCustom;
    }
}
