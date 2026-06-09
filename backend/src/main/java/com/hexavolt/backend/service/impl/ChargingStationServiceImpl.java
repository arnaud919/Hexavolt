package com.hexavolt.backend.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hexavolt.backend.dto.ChargingStationCreateDTO;
import com.hexavolt.backend.dto.ChargingStationDetailDTO;
import com.hexavolt.backend.dto.ChargingStationListDTO;
import com.hexavolt.backend.entity.ChargingStation;
import com.hexavolt.backend.entity.NicknameLocation;
import com.hexavolt.backend.entity.Power;
import com.hexavolt.backend.entity.StatusChargingStation;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.mapper.ChargingStationMapper;
import com.hexavolt.backend.repository.ChargingStationRepository;
import com.hexavolt.backend.repository.DayOfWeekRepository;
import com.hexavolt.backend.repository.NicknameLocationRepository;
import com.hexavolt.backend.repository.PowerRepository;
import com.hexavolt.backend.repository.StatusChargingStationRepository;
import com.hexavolt.backend.repository.WeeklyScheduleRepository;
import com.hexavolt.backend.service.ChargingStationService;
import com.hexavolt.backend.service.FileStorageService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ChargingStationServiceImpl implements ChargingStationService {

        private final ChargingStationRepository stationRepo;
        private final NicknameLocationRepository nicknameLocationRepo;
        private final PowerRepository powerRepo;
        private final FileStorageService fileStorageService;
        private final StatusChargingStationRepository statusChargingStationRepo;

        public ChargingStationServiceImpl(
                        ChargingStationRepository stationRepo,
                        NicknameLocationRepository nicknameLocationRepo,
                        PowerRepository powerRepo,
                        DayOfWeekRepository dayOfWeekRepo,
                        WeeklyScheduleRepository weeklyScheduleRepo,
                        FileStorageService fileStorageService,
                        StatusChargingStationRepository statusChargingStationRepo) {
                this.stationRepo = stationRepo;
                this.nicknameLocationRepo = nicknameLocationRepo;
                this.powerRepo = powerRepo;
                this.fileStorageService = fileStorageService;
                this.statusChargingStationRepo = statusChargingStationRepo;
        }

        @Override
        @Transactional
        public void create(
                        ChargingStationCreateDTO dto,
                        MultipartFile photo,
                        MultipartFile video) {
                ChargingStation station = new ChargingStation();

                User user = (User) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                Long locationId = Objects.requireNonNull(
                                dto.getLocationId(),
                                "Location id is required");

                NicknameLocation nl = nicknameLocationRepo
                                .findByStationLocationIdAndUser(locationId, user)
                                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

                Long powerId = Objects.requireNonNull(
                                dto.getPowerId(),
                                "Power id is required");

                Power power = powerRepo.findById(powerId)
                                .orElseThrow(() -> new IllegalArgumentException("Power not found"));

                StatusChargingStation activeStatus = statusChargingStationRepo.findByName("ACTIVE")
                                .orElseThrow(() -> new IllegalStateException("Status ACTIVE not found"));

                station.setStatus(activeStatus);

                station.setName(dto.getName());
                station.setHourlyRate(dto.getHourlyRate());
                station.setInstruction(dto.getInstruction());
                station.setIsCustom(dto.isCustom());
                station.setPower(power);
                station.setLocation(nl.getStationLocation());
                station.setLatitude(dto.getLatitude());
                station.setLongitude(dto.getLongitude());
                station.setStatus(activeStatus);

                if (photo != null && !photo.isEmpty()) {
                        String storedPhotoName = fileStorageService.storeChargingStationPhoto(photo);
                        System.out.println("PHOTO STORED NAME : " + storedPhotoName);
                        station.setPhotoName(storedPhotoName);
                }

                if (video != null && !video.isEmpty()) {
                        station.setVideoName(fileStorageService.storeChargingStationVideo(video));
                }

                System.out.println("PHOTO NULL ? " + (photo == null));
                System.out.println("PHOTO EMPTY ? " + (photo != null && photo.isEmpty()));
                System.out.println("PHOTO ORIGINAL NAME : " + (photo != null ? photo.getOriginalFilename() : "null"));

                System.out.println("VIDEO NULL ? " + (video == null));
                System.out.println("VIDEO EMPTY ? " + (video != null && video.isEmpty()));
                System.out.println("VIDEO ORIGINAL NAME : " + (video != null ? video.getOriginalFilename() : "null"));

                stationRepo.save(station);
        }

        @Override
        public List<ChargingStationListDTO> findByLocationId(Long locationId) {

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
                                                station.getName(),
                                                station.getPower().getKvaPower(),
                                                station.getHourlyRate(),
                                                station.getIsCustom()))
                                .toList();
        }

        @Override
        public List<ChargingStationListDTO> findMyChargingStations() {

                User user = (User) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                List<ChargingStation> stations = stationRepo.findByLocationUser(user);

                return stations.stream()
                                .map(ChargingStationMapper::toListDTO)
                                .toList();
        }

        @Override
        public ChargingStationDetailDTO findMyChargingStationById(Long id) {

                User user = (User) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                ChargingStation station = stationRepo
                                .findByIdAndLocationUser(id, user)
                                .orElseThrow(() -> new IllegalArgumentException("Charging station not found"));

                NicknameLocation nicknameLocation = nicknameLocationRepo
                                .findByStationLocationIdAndUser(station.getLocation().getId(), user)
                                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

                String photoUrl = station.getPhotoName() != null
                                ? "/api/public/stations/" + station.getId() + "/photo"
                                : null;

                String videoUrl = station.getVideoName() != null
                                ? "/api/public/stations/" + station.getId() + "/video"
                                : null;

                return new ChargingStationDetailDTO(
                                station.getId(),
                                station.getName(),
                                station.getPower().getKvaPower(),
                                station.getHourlyRate(),
                                station.getInstruction(),
                                station.getLatitude(),
                                station.getLongitude(),
                                station.getIsCustom(),
                                station.getStatus() != null ? station.getStatus().getName() : null,
                                station.getLocation().getAddress(),
                                station.getLocation().getCity().getName(),
                                photoUrl,
                                videoUrl,
                                nicknameLocation.getNickname());
        }

        @Override
        public void deleteMyChargingStation(Long id) {

                User user = (User) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                ChargingStation station = stationRepo.findByIdAndLocationUser(id, user)
                                .orElseThrow(() -> new IllegalArgumentException("Charging station not found"));

                stationRepo.delete(station);
        }
}
