package com.hexavolt.backend.service;

import com.hexavolt.backend.dto.AddressGeocodeResultDTO;

public interface GeocodingService {

    AddressGeocodeResultDTO geocodeAddress(String address, String postalCode, String cityName);
}