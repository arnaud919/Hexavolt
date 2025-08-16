package com.hexavolt.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "charging_station_reservation")
public class ChargingStationReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "charging_station", referencedColumnName = "id")
    private ChargingStation chargingStation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation", referencedColumnName = "id")
    private Reservation reservation;

    // --- Constructors ---
    public ChargingStationReservation() {
    }

    public ChargingStationReservation(ChargingStation chargingStation, Reservation reservation) {
        this.chargingStation = chargingStation;
        this.reservation = reservation;
    }

    // --- Getters and Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ChargingStation getChargingStation() {
        return chargingStation;
    }

    public void setChargingStation(ChargingStation chargingStation) {
        this.chargingStation = chargingStation;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
