package com.hexavolt.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexavolt.backend.dto.PowerDTO;
import com.hexavolt.backend.service.PowerService;

@RestController
@RequestMapping("/api/powers")
public class PowerController {
    private final PowerService powerService;

    public PowerController(PowerService powerService) {
        this.powerService = powerService;
    }

    @GetMapping
    public List<PowerDTO> getAll() {
        return powerService.findAll();
    }
}
