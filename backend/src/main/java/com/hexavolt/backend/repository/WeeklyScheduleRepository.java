package com.hexavolt.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.DayOfWeek;
import com.hexavolt.backend.entity.WeeklySchedule;

public interface WeeklyScheduleRepository
    extends JpaRepository<WeeklySchedule, Long> {

  Optional<WeeklySchedule> findByChargingStationIdAndDayOfWeek(
      Long chargingStationId,
      DayOfWeek dayOfWeek);
}
