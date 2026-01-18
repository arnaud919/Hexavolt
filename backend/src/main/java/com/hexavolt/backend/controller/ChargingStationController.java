package com.hexavolt.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexavolt.backend.dto.ChargingStationCreateDTO;
import com.hexavolt.backend.service.ChargingStationService;

@RestController
@RequestMapping("/api/stations")
public class ChargingStationController {

    private final ChargingStationService service;

    public ChargingStationController(ChargingStationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody ChargingStationCreateDTO dto) {

        service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

