package com.hexavolt.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hexavolt.backend.dto.ReservationCreateDTO;
import com.hexavolt.backend.service.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Void> createReservation(
            @Valid @RequestBody ReservationCreateDTO dto
    ) {
        reservationService.createReservation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}