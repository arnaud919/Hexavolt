package com.hexavolt.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.DayOfWeek;

public interface DayOfWeekRepository extends JpaRepository<DayOfWeek, Short> {
    Optional<DayOfWeek> findByName(String name);
}
