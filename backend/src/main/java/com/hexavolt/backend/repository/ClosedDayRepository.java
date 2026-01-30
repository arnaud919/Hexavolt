package com.hexavolt.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.ClosedDay;

public interface ClosedDayRepository extends JpaRepository<ClosedDay, Long> {
  // boolean existsByStation_IdAndDate(Integer stationId, LocalDate date);
  // List<ClosedDay> findByStation_IdAndDateBetween(Integer stationId, LocalDate from, LocalDate to);
}

