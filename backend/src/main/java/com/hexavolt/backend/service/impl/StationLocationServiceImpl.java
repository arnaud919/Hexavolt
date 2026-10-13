package com.hexavolt.backend.service.impl;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hexavolt.backend.dto.AddressGeocodeResultDTO;
import com.hexavolt.backend.dto.LocationDetailDTO;
import com.hexavolt.backend.dto.LocationListDTO;
import com.hexavolt.backend.dto.StationLocationCreateDTO;
import com.hexavolt.backend.dto.StationLocationUpdateDTO;
import com.hexavolt.backend.entity.ChargingStation;
import com.hexavolt.backend.entity.City;
import com.hexavolt.backend.entity.NicknameLocation;
import com.hexavolt.backend.entity.StationLocation;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.exception.BusinessException;
import com.hexavolt.backend.exception.ResourceNotFoundException;
import com.hexavolt.backend.repository.ChargingStationRepository;
import com.hexavolt.backend.repository.CityRepository;
import com.hexavolt.backend.repository.NicknameLocationRepository;
import com.hexavolt.backend.repository.StationLocationRepository;
import com.hexavolt.backend.service.GeocodingService;
import com.hexavolt.backend.service.StationLocationService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StationLocationServiceImpl implements StationLocationService {

        private final StationLocationRepository stationLocationRepo;
        private final NicknameLocationRepository nicknameLocationRepo;
        private final CityRepository cityRepo;
        private final GeocodingService geocodingService;
        private final ChargingStationRepository chargingStationRepo;

        private static final double MAX_DISTANCE_FROM_GEOCODED_ADDRESS_METERS = 300.0;
        private static final String STATUS_STATION_ACTIVE = "ACTIVE";
        private static final double MAX_DISTANCE_FROM_LOCATION_METERS = 500.0;

        public StationLocationServiceImpl(
                        StationLocationRepository stationLocationRepo,
                        NicknameLocationRepository nicknameLocationRepo,
                        CityRepository cityRepo,
                        GeocodingService geocodingService,
                        ChargingStationRepository chargingStationRepo) {
                this.stationLocationRepo = stationLocationRepo;
                this.nicknameLocationRepo = nicknameLocationRepo;
                this.cityRepo = cityRepo;
                this.geocodingService = geocodingService;
                this.chargingStationRepo = chargingStationRepo;
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

                validateLocationCloseToGeocodedAddress(
                                dto.getAddress(),
                                dto.getPostalCode(),
                                city,
                                dto.getLatitude(),
                                dto.getLongitude());

                StationLocation location = new StationLocation();
                location.setAddress(dto.getAddress());
                location.setPostalCode(dto.getPostalCode());
                location.setCity(city);
                location.setUser(user);
                location.setLatitude(dto.getLatitude());
                location.setLongitude(dto.getLongitude());

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

                return toDetailDTO(nicknameLocation);
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

        @Override
        @Transactional
        public LocationDetailDTO updateMyLocation(Long locationId, StationLocationUpdateDTO dto) {

                User user = getCurrentUser();

                requireId(locationId, "L'identifiant de l'emplacement est obligatoire.");

                if (dto == null) {
                        throw new BusinessException("Les informations de l'emplacement sont obligatoires.");
                }

                requireId(dto.getCityId(), "La ville est obligatoire.");

                NicknameLocation nicknameLocation = nicknameLocationRepo
                                .findByStationLocationIdAndUser(locationId, user)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Emplacement introuvable ou inaccessible."));

                City city = cityRepo.findById(dto.getCityId())
                                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable."));

                StationLocation location = nicknameLocation.getStationLocation();

                Double oldLatitude = location.getLatitude();
                Double oldLongitude = location.getLongitude();

                validateLocationCloseToGeocodedAddress(
                                dto.getAddress(),
                                dto.getPostalCode(),
                                city,
                                dto.getLatitude(),
                                dto.getLongitude());

                List<ChargingStation> activeStations = chargingStationRepo
                                .findByLocationIdAndStatusName(location.getId(), STATUS_STATION_ACTIVE);

                if (activeStations.isEmpty()) {
                        moveAttachedStationsWithLocation(
                                        location.getId(),
                                        oldLatitude,
                                        oldLongitude,
                                        dto.getLatitude(),
                                        dto.getLongitude());
                } else {
                        validateActiveStationsRemainCloseToLocation(
                                        activeStations,
                                        dto.getLatitude(),
                                        dto.getLongitude());
                }

                location.setAddress(dto.getAddress());
                location.setPostalCode(dto.getPostalCode());
                location.setCity(city);
                location.setLatitude(dto.getLatitude());
                location.setLongitude(dto.getLongitude());

                nicknameLocation.setNickname(dto.getNickname());

                stationLocationRepo.save(location);
                nicknameLocationRepo.save(nicknameLocation);

                return toDetailDTO(nicknameLocation);
        }

        private LocationDetailDTO toDetailDTO(NicknameLocation nicknameLocation) {
                StationLocation location = nicknameLocation.getStationLocation();
                City city = location.getCity();

                return new LocationDetailDTO(
                                location.getId(),
                                nicknameLocation.getNickname(),
                                location.getAddress(),
                                location.getPostalCode(),
                                city.getId(),
                                city.getName(),
                                location.getLatitude(),
                                location.getLongitude());
        }

        private double calculateDistanceInMeters(
                        double latitude1,
                        double longitude1,
                        double latitude2,
                        double longitude2) {
                final double earthRadiusMeters = 6371000;

                double lat1Rad = Math.toRadians(latitude1);
                double lat2Rad = Math.toRadians(latitude2);

                double deltaLat = Math.toRadians(latitude2 - latitude1);
                double deltaLon = Math.toRadians(longitude2 - longitude1);

                double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                                                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

                return earthRadiusMeters * c;
        }

        private void validateActiveStationsRemainCloseToLocation(
                        List<ChargingStation> activeStations,
                        Double newLatitude,
                        Double newLongitude) {

                for (ChargingStation station : activeStations) {
                        if (station.getLatitude() == null || station.getLongitude() == null) {
                                throw new BusinessException(
                                                "Une borne active rattachée à ce lieu n'a pas de position.");
                        }

                        double distance = calculateDistanceInMeters(
                                        newLatitude,
                                        newLongitude,
                                        station.getLatitude(),
                                        station.getLongitude());

                        if (distance > MAX_DISTANCE_FROM_LOCATION_METERS) {
                                throw new BusinessException(
                                                "Ce lieu possède des bornes actives. La nouvelle adresse est trop éloignée des bornes existantes.");
                        }
                }
        }

        private void validateLocationCloseToGeocodedAddress(
                        String address,
                        String postalCode,
                        City city,
                        Double latitude,
                        Double longitude) {
                if (latitude == null || longitude == null) {
                        throw new BusinessException("La position du lieu est obligatoire.");
                }

                AddressGeocodeResultDTO geocodedAddress = geocodingService.geocodeAddress(
                                address,
                                postalCode,
                                city.getName());

                double distance = calculateDistanceInMeters(
                                geocodedAddress.getLatitude(),
                                geocodedAddress.getLongitude(),
                                latitude,
                                longitude);

                if (distance > MAX_DISTANCE_FROM_GEOCODED_ADDRESS_METERS) {
                        throw new BusinessException(
                                        "La position du lieu doit être proche de l'adresse renseignée.");
                }
        }

        private void moveAttachedStationsWithLocation(
                        Long locationId,
                        Double oldLatitude,
                        Double oldLongitude,
                        Double newLatitude,
                        Double newLongitude) {

                if (oldLatitude == null || oldLongitude == null) {
                        throw new BusinessException("L'ancienne position du lieu est introuvable.");
                }

                if (newLatitude == null || newLongitude == null) {
                        throw new BusinessException("La nouvelle position du lieu est obligatoire.");
                }

                List<ChargingStation> stations = chargingStationRepo.findByLocationId(locationId);

                double latitudeDelta = newLatitude - oldLatitude;
                double longitudeDelta = newLongitude - oldLongitude;

                for (ChargingStation station : stations) {
                        if (station.getLatitude() != null && station.getLongitude() != null) {
                                station.setLatitude(station.getLatitude() + latitudeDelta);
                                station.setLongitude(station.getLongitude() + longitudeDelta);
                        }
                }
        }

}