package com.hexavolt.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.WeeklySchedule;

public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, Long> {
  List<WeeklySchedule> findByChargingStationId(Long stationId);
  // List<WeeklySchedule> findByStation_Id(Integer stationId);
}

