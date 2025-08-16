package com.hexavolt.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.Power;

public interface PowerRepository extends JpaRepository<Power, Integer> {
  // Optional<Power> findByKw(Integer kvaPower);
}
