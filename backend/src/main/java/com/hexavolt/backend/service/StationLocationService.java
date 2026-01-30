package com.hexavolt.backend.service;

import java.util.List;

import com.hexavolt.backend.dto.LocationDetailDTO;
import com.hexavolt.backend.dto.LocationListDTO;
import com.hexavolt.backend.dto.StationLocationCreateDTO;

public interface StationLocationService {
    void create(StationLocationCreateDTO dto);

    List<LocationListDTO> findMyLocations();

    LocationDetailDTO findMyLocationById(Long id);

}
