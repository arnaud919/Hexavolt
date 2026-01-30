package com.hexavolt.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.StationLocation;

public interface StationLocationRepository extends JpaRepository<StationLocation, Long> {
    
}
