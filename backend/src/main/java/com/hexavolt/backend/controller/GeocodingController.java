package com.hexavolt.backend.controller;

import com.hexavolt.backend.dto.AddressGeocodeResultDTO;
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
            CityRepository cityRepository
    ) {
        this.geocodingService = geocodingService;
        this.cityRepository = cityRepository;
    }

    @GetMapping("/api/geocoding/address")
    public AddressGeocodeResultDTO geocodeAddress(
            @RequestParam String address,
            @RequestParam String postalCode,
            @RequestParam Long cityId
    ) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable."));

        return geocodingService.geocodeAddress(
                address,
                postalCode,
                city.getName()
        );
    }
}