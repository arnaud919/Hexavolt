package com.hexavolt.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexavolt.backend.dto.LocationDetailDTO;
import com.hexavolt.backend.dto.LocationListDTO;
import com.hexavolt.backend.dto.StationLocationCreateDTO;
import com.hexavolt.backend.service.StationLocationService;

@RestController
@RequestMapping("/api/locations")
public class StationLocationController {

    private final StationLocationService service;

    public StationLocationController(StationLocationService service) {
        this.service = service;
    }

    // 🔹 Liste des lieux de l'utilisateur
    @GetMapping
    public List<LocationListDTO> myLocations() {
        return service.findMyLocations();
    }

    // 🔹 Détail d’un lieu de l'utilisateur
    @GetMapping("/{id}")
    public LocationDetailDTO getMyLocation(@PathVariable Integer id) {
        return service.findMyLocationById(id);
    }

    // 🔹 Création d’un lieu
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody StationLocationCreateDTO dto) {
        service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    
}

