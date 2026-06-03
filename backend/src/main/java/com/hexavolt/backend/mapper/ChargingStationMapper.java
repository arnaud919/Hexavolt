package com.hexavolt.backend.mapper;

import com.hexavolt.backend.dto.ChargingStationListDTO;
import com.hexavolt.backend.entity.ChargingStation;

public final class ChargingStationMapper {

    private ChargingStationMapper() {
    }

    public static ChargingStationListDTO toListDTO(
            ChargingStation station) {

        return new ChargingStationListDTO(
                station.getId(),
                station.getName(),
                station.getPower().getKvaPower(),
                station.getHourlyRate(),
                station.getIsCustom(),
                station.getLocation().getAddress(),
                station.getStatus() != null
                        ? station.getStatus().getName()
                        : null,
                station.getPhotoName() != null
                        ? "/api/public/stations/" + station.getId() + "/photo"
                        : null);
    }
}