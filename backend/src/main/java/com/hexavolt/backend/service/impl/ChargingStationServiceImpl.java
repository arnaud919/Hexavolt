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
public class ChargingStationServiceImpl
                implements ChargingStationService {

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

                stationRepo.save(station);
        }

        @Override
        public List<ChargingStationListDTO> findByLocation(Integer locationId) {

                User user = (User) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                // 🔒 Vérification de propriété du lieu
                nicknameLocationRepo
                                .findByStationLocationIdAndUser(locationId, user)
                                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

                // 📦 Récupération des bornes du lieu
                return stationRepo.findByLocationId(locationId).stream()
                                .map(station -> new ChargingStationListDTO(
                                                station.getId(),
                                                station.getPower().getKvaPower(), // 👈 Power est une entité
                                                station.getHourlyRate(),
                                                station.getIsCustom()))
                                .toList();
        }

}
