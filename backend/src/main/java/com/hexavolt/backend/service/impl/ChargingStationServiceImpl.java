package com.hexavolt.backend.service.impl;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hexavolt.backend.dto.ChargingStationCreateDTO;
import com.hexavolt.backend.dto.ChargingStationListDTO;
import com.hexavolt.backend.entity.ChargingStation;
import com.hexavolt.backend.entity.NicknameLocation;
import com.hexavolt.backend.entity.Power;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.repository.ChargingStationRepository;
import com.hexavolt.backend.repository.NicknameLocationRepository;
import com.hexavolt.backend.repository.PowerRepository;
import com.hexavolt.backend.service.ChargingStationService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ChargingStationServiceImpl implements ChargingStationService {

    private final ChargingStationRepository stationRepo;
    private final NicknameLocationRepository nicknameLocationRepo;
    private final PowerRepository powerRepo;

    public ChargingStationServiceImpl(
            ChargingStationRepository stationRepo,
            NicknameLocationRepository nicknameLocationRepo,
            PowerRepository powerRepo) {
        this.stationRepo = stationRepo;
        this.nicknameLocationRepo = nicknameLocationRepo;
        this.powerRepo = powerRepo;
    }

    @Override
    @Transactional
    public void create(ChargingStationCreateDTO dto) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        NicknameLocation nl = nicknameLocationRepo
                .findByStationLocationIdAndUser(dto.getLocationId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

        Power power = powerRepo.findById(dto.getPowerId())
                .orElseThrow(() -> new IllegalArgumentException("Power not found"));

        ChargingStation station = new ChargingStation();
        station.setHourlyRate(dto.getHourlyRate());
        station.setInstruction(dto.getInstruction());
        station.setIsCustom(dto.isCustom());
        station.setPower(power);
        station.setLocation(nl.getStationLocation());
        station.setLatitude(dto.getLatitude());
        station.setLongitude(dto.getLongitude());

        stationRepo.save(station);
    }

    @Override
    public List<ChargingStationListDTO> findByLocationId(Integer locationId) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        nicknameLocationRepo
                .findByStationLocationIdAndUser(locationId, user)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

        return stationRepo.findByLocationId(locationId).stream()
                .map(station -> new ChargingStationListDTO(
                        station.getId(),
                        station.getPower().getKvaPower(),
                        station.getHourlyRate(),
                        station.getIsCustom()))
                .toList();
    }
}
