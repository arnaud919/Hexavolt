package com.hexavolt.backend.mapper;

import org.springframework.stereotype.Component;

import com.hexavolt.backend.dto.ChargingStationListDTO;
import com.hexavolt.backend.dto.ChargingStationSearchDTO;
import com.hexavolt.backend.entity.ChargingStation;

@Component
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

        public ChargingStationSearchDTO toSearchDTO(ChargingStation station) {
                String photoUrl = station.getPhotoName() != null
                                ? "/api/public/stations/" + station.getId() + "/photo"
                                : null;

                return new ChargingStationSearchDTO(
                                station.getId(),
                                station.getName(),
                                station.getPower().getKvaPower(),
                                station.getHourlyRate(),
                                station.getLocation().getCity().getName(),
                                station.getLocation().getAddress(),
                                photoUrl,
                                station.getLatitude(),
                                station.getLongitude());
        }
}