package com.hexavolt.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.StatusReservation;

public interface StatusReservationRepository
        extends JpaRepository<StatusReservation, Long> {

    Optional<StatusReservation> findByName(String name);

}
