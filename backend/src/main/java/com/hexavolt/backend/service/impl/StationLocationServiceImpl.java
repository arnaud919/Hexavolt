package com.hexavolt.backend.service.impl;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hexavolt.backend.dto.LocationDetailDTO;
import com.hexavolt.backend.dto.LocationListDTO;
import com.hexavolt.backend.dto.StationLocationCreateDTO;
import com.hexavolt.backend.entity.City;
import com.hexavolt.backend.entity.NicknameLocation;
import com.hexavolt.backend.entity.StationLocation;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.repository.CityRepository;
import com.hexavolt.backend.repository.NicknameLocationRepository;
import com.hexavolt.backend.repository.StationLocationRepository;
import com.hexavolt.backend.service.StationLocationService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StationLocationServiceImpl implements StationLocationService {

        private final StationLocationRepository stationLocationRepo;
        private final NicknameLocationRepository nicknameLocationRepo;
        private final CityRepository cityRepo;

        public StationLocationServiceImpl(
                        StationLocationRepository stationLocationRepo,
                        NicknameLocationRepository nicknameLocationRepo,
                        CityRepository cityRepo) {
                this.stationLocationRepo = stationLocationRepo;
                this.nicknameLocationRepo = nicknameLocationRepo;
                this.cityRepo = cityRepo;
        }

        @Override
        public void create(StationLocationCreateDTO dto) {

                User user = (User) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                City city = cityRepo.findById(dto.getCityId())
                                .orElseThrow(() -> new IllegalArgumentException("City not found"));

                StationLocation location = new StationLocation();
                location.setAddress(dto.getAddress());
                location.setPostalCode(dto.getPostalCode());
                location.setCity(city);
                location.setUser(user);

                stationLocationRepo.save(location);

                NicknameLocation nickname = new NicknameLocation();
                nickname.setNickname(dto.getNickname());
                nickname.setStationLocation(location);
                nickname.setUser(user);

                nicknameLocationRepo.save(nickname);
        }

        @Override
        public List<LocationListDTO> findMyLocations() {

                User user = (User) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                return nicknameLocationRepo.findByUser(user).stream()
                                .map(nl -> new LocationListDTO(
                                                nl.getStationLocation().getId(),
                                                nl.getNickname(),
                                                nl.getStationLocation().getAddress(),
                                                nl.getStationLocation().getPostalCode(),
                                                nl.getStationLocation().getCity().getName()))
                                .toList();
        }

        @Override
        public LocationDetailDTO findMyLocationById(Integer locationId) {

                User user = (User) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

                NicknameLocation nl = nicknameLocationRepo
                                .findByStationLocationIdAndUser(locationId, user)
                                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

                StationLocation loc = nl.getStationLocation();

                return new LocationDetailDTO(
                                loc.getId(),
                                nl.getNickname(),
                                loc.getAddress(),
                                loc.getPostalCode(),
                                loc.getCity().getName());
        }

}
