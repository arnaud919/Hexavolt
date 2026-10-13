package com.hexavolt.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hexavolt.backend.entity.ChargingStation;
import com.hexavolt.backend.entity.User;

public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {
  List<ChargingStation> findByLocationId(Long locationId);

  List<ChargingStation> findByLocationUser(User user);

  Optional<ChargingStation> findByIdAndLocationUser(Long id, User user);

  @Query("""
      SELECT cs
      FROM ChargingStation cs
      JOIN FETCH cs.power p
      JOIN FETCH cs.location l
      JOIN FETCH l.city c
      JOIN cs.status s
      WHERE s.name = 'ACTIVE'
      ORDER BY c.name ASC, cs.hourlyRate ASC
      """)
  List<ChargingStation> findActiveStationsForSearch();

  List<ChargingStation> findByLocationIdAndStatusName(Long locationId, String statusName);

  // Page<ChargingStation> findByOwner_Id(Integer ownerId, Pageable pageable);
  // Page<ChargingStation> findByActiveTrue(Pageable pageable);

  // Page<ChargingStation> findByActiveTrueAndLocation_CityIgnoreCase(String city,
  // Pageable p);
  // Page<ChargingStation> findByActiveTrueAndPower_KwGreaterThanEqual(Integer
  // minKw, Pageable p);
}
