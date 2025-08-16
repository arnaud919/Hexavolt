package com.hexavolt.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "nickname_location", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "station_location"})
})
public class NicknameLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String nickname;

    @ManyToOne(optional = false)
    @JoinColumn(name = "station_location", referencedColumnName = "id")
    private StationLocation stationLocation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // --- Constructors ---
    public NicknameLocation() {}

    public NicknameLocation(String nickname, StationLocation stationLocation, User user) {
        this.nickname = nickname;
        this.stationLocation = stationLocation;
        this.user = user;
    }

    // --- Getters and Setters ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public StationLocation getStationLocation() {
        return stationLocation;
    }

    public void setStationLocation(StationLocation stationLocation) {
        this.stationLocation = stationLocation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
