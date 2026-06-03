package com.hexavolt.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.StatusChargingStation;

public interface StatusChargingStationRepository extends JpaRepository<StatusChargingStation, Long> {

    Optional<StatusChargingStation> findByName(String name);
}
