package com.hexavolt.backend.service.impl;

import java.util.List;

import com.hexavolt.backend.dto.PowerDTO;
import com.hexavolt.backend.repository.PowerRepository;
import com.hexavolt.backend.service.PowerService;

public class PowerServiceImpl implements PowerService {
    private final PowerRepository powerRepo;

    public PowerServiceImpl(PowerRepository powerRepo) {
        this.powerRepo = powerRepo;
    }

    @Override
    public List<PowerDTO> findAll() {
        return powerRepo.findAll().stream()
            .map(p -> new PowerDTO(
                p.getId(),
                p.getKvaPower()
            ))
            .toList();
    }
}
