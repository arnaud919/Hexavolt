package com.hexavolt.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.City;

public interface CityRepository extends JpaRepository<City, Integer> {
    // Optional<City> findByNameIgnoreCase(String name);
}
