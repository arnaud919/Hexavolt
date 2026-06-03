package com.hexavolt.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexavolt.backend.entity.StatusChargingStation;
import com.hexavolt.backend.repository.StatusChargingStationRepository;

@RestController
@RequestMapping("/api/status-charging-stations")
public class StatusChargingStationController {
    private final StatusChargingStationRepository repo;

    public StatusChargingStationController(
            StatusChargingStationRepository repo
    ) {
        this.repo = repo;
    }

    @GetMapping
    public List<StatusChargingStation> findAll() {
        return repo.findAll();
    }
}
