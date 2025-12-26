package com.hexavolt.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.ChargingStation;

public interface ChargingStationRepository extends JpaRepository<ChargingStation, Integer> {
  // Page<ChargingStation> findByOwner_Id(Integer ownerId, Pageable pageable);
  // Page<ChargingStation> findByActiveTrue(Pageable pageable);

  // Page<ChargingStation> findByActiveTrueAndLocation_CityIgnoreCase(String city, Pageable p);
  // Page<ChargingStation> findByActiveTrueAndPower_KwGreaterThanEqual(Integer minKw, Pageable p);
}
