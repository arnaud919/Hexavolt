package com.hexavolt.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.ChargingStation;

public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {
  List<ChargingStation> findByLocationId(Long locationId);
  // Page<ChargingStation> findByOwner_Id(Integer ownerId, Pageable pageable);
  // Page<ChargingStation> findByActiveTrue(Pageable pageable);

  // Page<ChargingStation> findByActiveTrueAndLocation_CityIgnoreCase(String city, Pageable p);
  // Page<ChargingStation> findByActiveTrueAndPower_KwGreaterThanEqual(Integer minKw, Pageable p);
}
