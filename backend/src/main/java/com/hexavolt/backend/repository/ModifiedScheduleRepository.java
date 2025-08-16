package com.hexavolt.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.ModifiedSchedule;

public interface ModifiedScheduleRepository extends JpaRepository<ModifiedSchedule, Integer> {
  // List<ModifiedSchedule> findByStation_IdAndDate(Integer stationId, LocalDate date);
}

