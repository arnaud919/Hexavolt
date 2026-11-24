package com.hexavolt.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.ClosedDay;

public interface ClosedDayRepository extends JpaRepository<ClosedDay, Long> {

  List<ClosedDay> findByChargingStationIdOrderByDateAsc(Long stationId);

  Optional<ClosedDay> findByChargingStationIdAndDate(
        Long stationId,
        LocalDate date);

  boolean existsByChargingStationIdAndDate(
      Long chargingStationId,
      LocalDate date);
}
