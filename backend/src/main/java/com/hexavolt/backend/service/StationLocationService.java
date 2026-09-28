package com.hexavolt.backend.service;

import java.util.List;

import com.hexavolt.backend.dto.LocationDetailDTO;
import com.hexavolt.backend.dto.LocationListDTO;
import com.hexavolt.backend.dto.StationLocationCreateDTO;
import com.hexavolt.backend.dto.StationLocationUpdateDTO;

public interface StationLocationService {
    void create(StationLocationCreateDTO dto);

    List<LocationListDTO> findMyLocations();

    LocationDetailDTO findMyLocationById(Long id);

    LocationDetailDTO updateMyLocation(Long locationId, StationLocationUpdateDTO dto);
}
