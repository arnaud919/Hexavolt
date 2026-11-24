
package com.hexavolt.backend.controller;

import com.hexavolt.backend.dto.AvailableSlotDTO;
import com.hexavolt.backend.dto.ChargingStationPublicDetailDTO;
import com.hexavolt.backend.service.ChargingStationAvailableSlotService;
import com.hexavolt.backend.service.ChargingStationService;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/stations")
public class ChargingStationPublicController {

    private final ChargingStationService chargingStationService;
    private final ChargingStationAvailableSlotService availableSlotService;

    public ChargingStationPublicController(
            ChargingStationService chargingStationService,
        ChargingStationAvailableSlotService availableSlotService) {
        this.chargingStationService = chargingStationService;
        this.availableSlotService = availableSlotService;
    }

    @GetMapping("/{id}")
    public ChargingStationPublicDetailDTO getPublicChargingStationById(
            @PathVariable Long id) {

        return chargingStationService.findPublicChargingStationById(id);
    }

    @GetMapping("/{id}/availability")
    public List<AvailableSlotDTO> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam LocalDate date) {

        return availableSlotService.getAvailableSlots(id, date);
    }

}
