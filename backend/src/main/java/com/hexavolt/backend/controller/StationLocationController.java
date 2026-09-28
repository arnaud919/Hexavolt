package com.hexavolt.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexavolt.backend.dto.LocationDetailDTO;
import com.hexavolt.backend.dto.LocationListDTO;
import com.hexavolt.backend.dto.StationLocationCreateDTO;
import com.hexavolt.backend.dto.StationLocationUpdateDTO;
import com.hexavolt.backend.dto.ChargingStationListDTO;
import com.hexavolt.backend.service.StationLocationService;

import jakarta.validation.Valid;

import com.hexavolt.backend.service.ChargingStationService;

@RestController
@RequestMapping("/api/locations")
public class StationLocationController {

    private final StationLocationService locationService;
    private final ChargingStationService chargingStationService;

    public StationLocationController(
            StationLocationService locationService,
            ChargingStationService chargingStationService) {
        this.locationService = locationService;
        this.chargingStationService = chargingStationService;
    }

    // 🔹 Liste des lieux de l'utilisateur
    @GetMapping
    public List<LocationListDTO> myLocations() {
        return locationService.findMyLocations();
    }

    // 🔹 Détail d’un lieu de l'utilisateur
    @GetMapping("/{id}")
    public ResponseEntity<LocationDetailDTO> findMyLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.findMyLocationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDetailDTO> updateMyLocation(
            @PathVariable Long id,
            @Valid @RequestBody StationLocationUpdateDTO dto) {
        return ResponseEntity.ok(locationService.updateMyLocation(id, dto));
    }

    // 🔹 Création d’un lieu
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody StationLocationCreateDTO dto) {
        locationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 🔹 Liste des bornes d’un lieu
    @GetMapping("/{id}/stations")
    public List<ChargingStationListDTO> getStationsByLocation(
            @PathVariable Long id) {
        return chargingStationService.findByLocationId(id);
    }
}