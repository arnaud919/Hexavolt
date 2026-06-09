package com.hexavolt.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hexavolt.backend.dto.ChargingStationCreateDTO;
import com.hexavolt.backend.dto.ChargingStationDetailDTO;
import com.hexavolt.backend.dto.ChargingStationListDTO;
import com.hexavolt.backend.service.ChargingStationService;

@RestController
@RequestMapping("/api/stations")
public class ChargingStationController {

    private final ChargingStationService chargingStationService;

    public ChargingStationController(ChargingStationService chargingStationService) {
        this.chargingStationService = chargingStationService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> create(
            @RequestPart("data") ChargingStationCreateDTO dto,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "video", required = false) MultipartFile video) {

        chargingStationService.create(dto, photo, video);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/me")
    public List<ChargingStationListDTO> getMyStations() {
        return chargingStationService.findMyChargingStations();
    }

    @GetMapping("/{id}")
    public ChargingStationDetailDTO getMyChargingStationById(@PathVariable Long id) {
        return chargingStationService.findMyChargingStationById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyChargingStation(@PathVariable Long id) {
        chargingStationService.deleteMyChargingStation(id);
        return ResponseEntity.noContent().build();
    }
}