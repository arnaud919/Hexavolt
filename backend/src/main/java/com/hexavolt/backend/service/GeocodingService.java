
package com.hexavolt.backend.service;

import com.hexavolt.backend.dto.AddressGeocodeResultDTO;
import com.hexavolt.backend.dto.AddressReverseGeocodeResultDTO;

public interface GeocodingService {

    AddressGeocodeResultDTO geocodeAddress(
            String address,
            String postalCode,
            String cityName);

    AddressReverseGeocodeResultDTO reverseGeocode(
            double latitude,
            double longitude);
}
