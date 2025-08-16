package com.hexavolt.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "power")
public class Power {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "kva_power", precision = 6, scale = 2, nullable = false)
    private BigDecimal kvaPower;

    // --- Constructors ---
    public Power() {
    }

    public Power(BigDecimal kvaPower) {
        this.kvaPower = kvaPower;
    }

    // --- Getters and Setters ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getKvaPower() {
        return kvaPower;
    }

    public void setKvaPower(BigDecimal kvaPower) {
        this.kvaPower = kvaPower;
    }
}
