package com.hexavolt.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.WeeklySchedule;

public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, Integer> {
  // List<WeeklySchedule> findByStation_IdAndDayOfWeek(Integer stationId, int dayOfWeek);
  // List<WeeklySchedule> findByStation_Id(Integer stationId);
}

