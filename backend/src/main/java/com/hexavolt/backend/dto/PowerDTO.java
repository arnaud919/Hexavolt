package com.hexavolt.backend.dto;

import java.math.BigDecimal;

public class PowerDTO {
    private Integer id;
    private BigDecimal kvaPower;

    public PowerDTO(Integer id, BigDecimal kvaPower) {
        this.id = id;
        this.kvaPower = kvaPower;
    }

    public Integer getId() {
        return id;
    }

    public BigDecimal getKvaPower() {
        return kvaPower;
    }
}
