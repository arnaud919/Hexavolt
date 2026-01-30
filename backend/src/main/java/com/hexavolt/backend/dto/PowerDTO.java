package com.hexavolt.backend.dto;

import java.math.BigDecimal;

public class PowerDTO {
    private Long id;
    private BigDecimal kvaPower;

    public PowerDTO(Long id, BigDecimal kvaPower) {
        this.id = id;
        this.kvaPower = kvaPower;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getKvaPower() {
        return kvaPower;
    }
}
