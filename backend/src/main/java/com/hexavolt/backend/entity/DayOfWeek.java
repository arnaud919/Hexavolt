package com.hexavolt.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "day_of_week")
public class DayOfWeek {

    @Id
    private Short id; // SMALLINT en base

    @Column(length = 20, nullable = false, unique = true)
    private String name;

    // --- Constructors ---

    public DayOfWeek() {}

    public DayOfWeek(Short id, String name) {
        this.id = id;
        this.name = name;
    }

    // --- Getters and Setters ---

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
