package com.hexavolt.backend.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.ModifiedSchedule;

public interface ModifiedScheduleRepository
    extends JpaRepository<ModifiedSchedule, Long> {

  Optional<ModifiedSchedule> findByChargingStationIdAndDate(
      Long chargingStationId,
      LocalDate date);
}
