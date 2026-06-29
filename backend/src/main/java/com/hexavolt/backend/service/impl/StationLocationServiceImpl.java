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
import com.hexavolt.backend.exception.BusinessException;
import com.hexavolt.backend.exception.ResourceNotFoundException;
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
                if (dto == null) {
                        throw new BusinessException("Les informations de l'emplacement sont obligatoires.");
                }

                User user = getCurrentUser();

                Long cityId = requireId(dto.getCityId(), "La ville est obligatoire.");

                City city = cityRepo.findById(cityId)
                                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable."));

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
                User user = getCurrentUser();

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
        public LocationDetailDTO findMyLocationById(Long locationId) {
                User user = getCurrentUser();

                Long validLocationId = requireId(
                                locationId,
                                "L'identifiant de l'emplacement est obligatoire.");

                NicknameLocation nicknameLocation = nicknameLocationRepo
                                .findByStationLocationIdAndUser(validLocationId, user)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Emplacement introuvable ou inaccessible."));

                StationLocation location = nicknameLocation.getStationLocation();

                return new LocationDetailDTO(
                                location.getId(),
                                nicknameLocation.getNickname(),
                                location.getAddress(),
                                location.getPostalCode(),
                                location.getCity().getName());
        }

        private User getCurrentUser() {
                return (User) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();
        }

        private Long requireId(Long id, String message) {
                if (id == null) {
                        throw new BusinessException(message);
                }

                return id;
        }
}