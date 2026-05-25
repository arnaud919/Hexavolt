package com.hexavolt.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexavolt.backend.dto.ChargingStationCreateDTO;
import com.hexavolt.backend.dto.ChargingStationListDTO;
import com.hexavolt.backend.service.ChargingStationService;

@RestController
@RequestMapping("/api/stations")
public class ChargingStationController {

    private final ChargingStationService chargingStationService;

    public ChargingStationController(ChargingStationService chargingStationService) {
        this.chargingStationService = chargingStationService;
    }

    // 🔹 Création d’une borne
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody ChargingStationCreateDTO dto) {
        chargingStationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/me")
    public List<ChargingStationListDTO> getMyStations(Authentication authentication) {

        return chargingStationService.findMyChargingStations();
    }
}
