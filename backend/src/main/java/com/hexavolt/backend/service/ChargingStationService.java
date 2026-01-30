package com.hexavolt.backend.service;

import java.util.List;

import com.hexavolt.backend.dto.ChargingStationCreateDTO;
import com.hexavolt.backend.dto.ChargingStationListDTO;

public interface ChargingStationService {
    
    void create(ChargingStationCreateDTO dto);

    List<ChargingStationListDTO> findByLocationId(Long id);

}
