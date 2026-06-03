package com.hexavolt.backend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.hexavolt.backend.dto.ChargingStationCreateDTO;
import com.hexavolt.backend.dto.ChargingStationDetailDTO;
import com.hexavolt.backend.dto.ChargingStationListDTO;

public interface ChargingStationService {

    void create(
            ChargingStationCreateDTO dto,
            MultipartFile photo,
            MultipartFile video);

    List<ChargingStationListDTO> findByLocationId(Long id);

    List<ChargingStationListDTO> findMyChargingStations();

    ChargingStationDetailDTO findMyChargingStationById(Long id);

}
