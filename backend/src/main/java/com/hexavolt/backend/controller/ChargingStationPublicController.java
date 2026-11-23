
package com.hexavolt.backend.controller;

import com.hexavolt.backend.dto.ChargingStationPublicDetailDTO;
import com.hexavolt.backend.service.ChargingStationService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/stations")
public class ChargingStationPublicController {

    private final ChargingStationService chargingStationService;

    public ChargingStationPublicController(
            ChargingStationService chargingStationService) {
        this.chargingStationService = chargingStationService;
    }

    @GetMapping("/{id}")
    public ChargingStationPublicDetailDTO getPublicChargingStationById(
            @PathVariable Long id) {

        return chargingStationService.findPublicChargingStationById(id);
    }
}
