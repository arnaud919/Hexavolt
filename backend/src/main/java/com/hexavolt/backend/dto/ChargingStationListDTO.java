package com.hexavolt.backend.dto;

import java.math.BigDecimal;

public class ChargingStationListDTO {

    private Integer id;
    private BigDecimal  power;
    private BigDecimal hourlyRate;
    private boolean isCustom;

    public ChargingStationListDTO(Integer id,
            BigDecimal  power,
            BigDecimal hourlyRate,
            boolean isCustom) {
        this.id = id;
        this.power = power;
        this.hourlyRate = hourlyRate;
        this.isCustom = isCustom;
    }

    public Integer getId() {
        return id;
    }

    public BigDecimal  getPower() {
        return power;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public boolean isCustom() {
        return isCustom;
    }
}
