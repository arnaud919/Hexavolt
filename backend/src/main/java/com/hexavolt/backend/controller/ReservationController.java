package com.hexavolt.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexavolt.backend.dto.MyReservationResponseDTO;
import com.hexavolt.backend.dto.OwnerReservationResponseDTO;
import com.hexavolt.backend.dto.ReservationCreateRequestDTO;
import com.hexavolt.backend.dto.ReservationResponseDTO;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.service.AuthService;
import com.hexavolt.backend.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final AuthService authService;

    public ReservationController(
            ReservationService reservationService,
            AuthService authService) {
        this.reservationService = reservationService;
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<MyReservationResponseDTO>> getMyReservations() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                reservationService.getMyReservations(user));
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @RequestBody ReservationCreateRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User connectedUser = (User) authentication.getPrincipal();

        ReservationResponseDTO response = reservationService.createReservation(request, connectedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long id) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        reservationService.confirmReservation(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        reservationService.rejectReservation(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owner/pending")
    public ResponseEntity<List<OwnerReservationResponseDTO>> getReservationsToProcess() {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(
                reservationService.getReservationsToProcess(user));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long id) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        reservationService.completeReservation(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/absence")
    public ResponseEntity<Void> markAbsent(@PathVariable Long id) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        reservationService.markReservationAsAbsent(id, user);
        return ResponseEntity.noContent().build();
    }
}
