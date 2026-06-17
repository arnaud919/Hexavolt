package com.hexavolt.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.DayOfWeek;
import com.hexavolt.backend.entity.WeeklySchedule;

public interface WeeklyScheduleRepository
        extends JpaRepository<WeeklySchedule, Long> {

    List<WeeklySchedule> findByChargingStationId(Long chargingStationId);

    void deleteByChargingStationId(Long chargingStationId);

    Optional<WeeklySchedule> findByChargingStationIdAndDayOfWeek(
            Long chargingStationId,
            DayOfWeek dayOfWeek);
}
