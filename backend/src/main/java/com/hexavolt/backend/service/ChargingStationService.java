package com.hexavolt.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.hexavolt.backend.dto.ChargingStationCreateDTO;
import com.hexavolt.backend.dto.ChargingStationDetailDTO;
import com.hexavolt.backend.dto.ChargingStationListDTO;
import com.hexavolt.backend.dto.ChargingStationPublicDetailDTO;
import com.hexavolt.backend.dto.ChargingStationUpdateDTO;
import com.hexavolt.backend.dto.ClosedDayDTO;
import com.hexavolt.backend.dto.ModifiedScheduleDTO;
import com.hexavolt.backend.dto.WeeklyScheduleDTO;
import com.hexavolt.backend.dto.ChargingStationEditDTO;
import com.hexavolt.backend.dto.ChargingStationSearchDTO;

public interface ChargingStationService {

    void create(
            ChargingStationCreateDTO dto,
            MultipartFile photo,
            MultipartFile video);

    List<ChargingStationListDTO> findByLocationId(Long id);

    List<ChargingStationListDTO> findMyChargingStations();

    ChargingStationDetailDTO findMyChargingStationById(Long id);

    void deleteMyChargingStation(Long id);

    void updateWeeklySchedule(Long stationId, List<WeeklyScheduleDTO> schedules);

    void updateMyChargingStation(Long id, ChargingStationUpdateDTO dto);

    ChargingStationEditDTO findMyChargingStationForEdit(Long id);

    List<ChargingStationSearchDTO> searchActiveStations();

    void withdrawMyChargingStation(Long id);

    ChargingStationPublicDetailDTO findPublicChargingStationById(Long id);

    List<ClosedDayDTO> getClosedDays(Long stationId);

    void addClosedDay(Long stationId, ClosedDayDTO dto);

    void deleteClosedDay(Long stationId, LocalDate date);

    List<ModifiedScheduleDTO> getModifiedSchedules(Long stationId);

    void saveModifiedSchedule(Long stationId, ModifiedScheduleDTO dto);

    void deleteModifiedSchedule(Long stationId, LocalDate date);

}
