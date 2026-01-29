package com.hexavolt.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.DayOfWeek;

public interface DayOfWeekRepository extends JpaRepository<DayOfWeek, Short> {
    
}
