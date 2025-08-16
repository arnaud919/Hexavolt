package com.hexavolt.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.ClosedDay;

public interface ClosedDayRepository extends JpaRepository<ClosedDay, Integer> {
  // boolean existsByStation_IdAndDate(Integer stationId, LocalDate date);
  // List<ClosedDay> findByStation_IdAndDateBetween(Integer stationId, LocalDate from, LocalDate to);
}

