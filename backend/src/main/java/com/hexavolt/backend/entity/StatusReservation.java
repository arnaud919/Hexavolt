package com.hexavolt.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "status_reservation", uniqueConstraints = {
    @UniqueConstraint(columnNames = "name")
})
public class StatusReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 10, nullable = false)
    private String name;

    // --- Constructors ---
    public StatusReservation() {
    }

    public StatusReservation(String name) {
        this.name = name;
    }

    // --- Getters and Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
