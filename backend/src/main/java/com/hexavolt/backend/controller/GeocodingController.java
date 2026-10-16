package com.hexavolt.backend.controller;

import com.hexavolt.backend.dto.AddressGeocodeResultDTO;
import com.hexavolt.backend.dto.AddressReverseGeocodeResultDTO;
import com.hexavolt.backend.entity.City;
import com.hexavolt.backend.exception.ResourceNotFoundException;
import com.hexavolt.backend.repository.CityRepository;
import com.hexavolt.backend.service.GeocodingService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeocodingController {

        private final GeocodingService geocodingService;
        private final CityRepository cityRepository;

        public GeocodingController(
                        GeocodingService geocodingService,
                        CityRepository cityRepository) {
                this.geocodingService = geocodingService;
                this.cityRepository = cityRepository;
        }

        @GetMapping("/api/geocoding/address")
        public AddressGeocodeResultDTO geocodeAddress(
                        @RequestParam String address,
                        @RequestParam String postalCode,
                        @RequestParam Long cityId) {
                City city = cityRepository.findById(cityId)
                                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable."));

                return geocodingService.geocodeAddress(
                                address,
                                postalCode,
                                city.getName());
        }

        @GetMapping("/api/geocoding/reverse")
        public AddressReverseGeocodeResultDTO reverseGeocode(
                        @RequestParam double latitude,
                        @RequestParam double longitude) {

                AddressReverseGeocodeResultDTO result = geocodingService.reverseGeocode(latitude, longitude);

                String cityCode = result.getCityCode();

                if (cityCode != null && !cityCode.isBlank()) {

                        String communeCode = getCommuneInseeCode(cityCode);

                        cityRepository.findByInseeCode(communeCode)
                                        .ifPresent(city -> result.setCityId(city.getId()));
                }

                return result;
        }

        private String getCommuneInseeCode(String cityCode) {

                if (!cityCode.matches("\\d{5}")) {
                        return cityCode;
                }

                int code = Integer.parseInt(cityCode);

                // Paris : 20 arrondissements
                if (code >= 75101 && code <= 75120) {
                        return "75056";
                }

                // Marseille : 16 arrondissements
                if (code >= 13201 && code <= 13216) {
                        return "13055";
                }

                // Lyon : 9 arrondissements
                if (code >= 69381 && code <= 69389) {
                        return "69123";
                }

                return cityCode;
        }

}