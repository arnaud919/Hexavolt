package com.hexavolt.backend.service.impl;

import java.time.LocalTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hexavolt.backend.dto.ChargingStationCreateDTO;
import com.hexavolt.backend.dto.ChargingStationDetailDTO;
import com.hexavolt.backend.dto.ChargingStationListDTO;
import com.hexavolt.backend.dto.WeeklyScheduleDTO;
import com.hexavolt.backend.entity.ChargingStation;
import com.hexavolt.backend.entity.DayOfWeek;
import com.hexavolt.backend.entity.NicknameLocation;
import com.hexavolt.backend.entity.Power;
import com.hexavolt.backend.entity.StatusChargingStation;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.entity.WeeklySchedule;
import com.hexavolt.backend.exception.BusinessException;
import com.hexavolt.backend.exception.ResourceNotFoundException;
import com.hexavolt.backend.mapper.ChargingStationMapper;
import com.hexavolt.backend.repository.ChargingStationRepository;
import com.hexavolt.backend.repository.DayOfWeekRepository;
import com.hexavolt.backend.repository.NicknameLocationRepository;
import com.hexavolt.backend.repository.PowerRepository;
import com.hexavolt.backend.repository.StatusChargingStationRepository;
import com.hexavolt.backend.repository.WeeklyScheduleRepository;
import com.hexavolt.backend.service.ChargingStationService;
import com.hexavolt.backend.service.FileStorageService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ChargingStationServiceImpl implements ChargingStationService {

        private final ChargingStationRepository stationRepo;
        private final NicknameLocationRepository nicknameLocationRepo;
        private final PowerRepository powerRepo;
        private final DayOfWeekRepository dayOfWeekRepo;
        private final WeeklyScheduleRepository weeklyScheduleRepo;
        private final FileStorageService fileStorageService;
        private final StatusChargingStationRepository statusChargingStationRepo;

        public ChargingStationServiceImpl(
                        ChargingStationRepository stationRepo,
                        NicknameLocationRepository nicknameLocationRepo,
                        PowerRepository powerRepo,
                        DayOfWeekRepository dayOfWeekRepo,
                        WeeklyScheduleRepository weeklyScheduleRepo,
                        FileStorageService fileStorageService,
                        StatusChargingStationRepository statusChargingStationRepo) {
                this.stationRepo = stationRepo;
                this.nicknameLocationRepo = nicknameLocationRepo;
                this.powerRepo = powerRepo;
                this.dayOfWeekRepo = dayOfWeekRepo;
                this.weeklyScheduleRepo = weeklyScheduleRepo;
                this.fileStorageService = fileStorageService;
                this.statusChargingStationRepo = statusChargingStationRepo;
        }

        @Override
        @Transactional
        public void create(
                        ChargingStationCreateDTO dto,
                        MultipartFile photo,
                        MultipartFile video) {

                if (dto == null) {
                        throw new BusinessException("Les informations de la borne sont obligatoires.");
                }

                User user = getCurrentUser();

                Long locationId = requireId(dto.getLocationId(), "L'emplacement est obligatoire.");
                Long powerId = requireId(dto.getPowerId(), "La puissance est obligatoire.");
                Long statusId = requireId(dto.getStatusId(), "Le statut est obligatoire.");

                NicknameLocation nicknameLocation = findUserLocation(locationId, user);
                Power power = findPower(powerId);
                StatusChargingStation status = findStatus(statusId);

                ChargingStation station = new ChargingStation();

                station.setName(dto.getName());
                station.setHourlyRate(dto.getHourlyRate());
                station.setInstruction(dto.getInstruction());
                station.setIsCustom(dto.isCustom());
                station.setPower(power);
                station.setLocation(nicknameLocation.getStationLocation());
                station.setLatitude(dto.getLatitude());
                station.setLongitude(dto.getLongitude());
                station.setStatus(status);

                if (photo != null && !photo.isEmpty()) {
                        String storedPhotoName = fileStorageService.storeChargingStationPhoto(photo);
                        station.setPhotoName(storedPhotoName);
                }

                if (video != null && !video.isEmpty()) {
                        String storedVideoName = fileStorageService.storeChargingStationVideo(video);
                        station.setVideoName(storedVideoName);
                }

                stationRepo.save(station);
        }

        @Override
        public List<ChargingStationListDTO> findByLocationId(Long locationId) {
                User user = getCurrentUser();

                Long validLocationId = requireId(locationId, "L'emplacement est obligatoire.");

                findUserLocation(validLocationId, user);

                return stationRepo.findByLocationId(validLocationId).stream()
                                .map(station -> new ChargingStationListDTO(
                                                station.getId(),
                                                station.getName(),
                                                station.getPower().getKvaPower(),
                                                station.getHourlyRate(),
                                                station.getIsCustom()))
                                .toList();
        }

        @Override
        public List<ChargingStationListDTO> findMyChargingStations() {
                User user = getCurrentUser();

                List<ChargingStation> stations = stationRepo.findByLocationUser(user);

                return stations.stream()
                                .map(ChargingStationMapper::toListDTO)
                                .toList();
        }

        @Override
        public ChargingStationDetailDTO findMyChargingStationById(Long id) {
                User user = getCurrentUser();

                Long stationId = requireId(id, "L'identifiant de la borne est obligatoire.");

                ChargingStation station = findOwnedStation(stationId, user);

                NicknameLocation nicknameLocation = findUserLocation(
                                station.getLocation().getId(),
                                user);

                String photoUrl = station.getPhotoName() != null
                                ? "/api/public/stations/" + station.getId() + "/photo"
                                : null;

                String videoUrl = station.getVideoName() != null
                                ? "/api/public/stations/" + station.getId() + "/video"
                                : null;

                List<WeeklyScheduleDTO> weeklySchedules = weeklyScheduleRepo
                                .findByChargingStationId(station.getId())
                                .stream()
                                .map(schedule -> {
                                        WeeklyScheduleDTO dto = new WeeklyScheduleDTO();
                                        dto.setDayOfWeekId(schedule.getDayOfWeek().getId().longValue());
                                        dto.setStartTime(schedule.getStartTime());
                                        dto.setEndTime(schedule.getEndTime());
                                        return dto;
                                })
                                .toList();

                return new ChargingStationDetailDTO(
                                station.getId(),
                                station.getName(),
                                station.getPower().getKvaPower(),
                                station.getHourlyRate(),
                                station.getInstruction(),
                                station.getLatitude(),
                                station.getLongitude(),
                                station.getIsCustom(),
                                station.getStatus() != null ? station.getStatus().getName() : null,
                                station.getLocation().getAddress(),
                                station.getLocation().getCity().getName(),
                                photoUrl,
                                videoUrl,
                                nicknameLocation.getNickname(),
                                weeklySchedules);
        }

        @Override
        public void deleteMyChargingStation(Long id) {
                User user = getCurrentUser();

                Long stationId = requireId(id, "L'identifiant de la borne est obligatoire.");

                ChargingStation station = findOwnedStation(stationId, user);

                stationRepo.delete(station);
        }

        @Override
        public void updateWeeklySchedule(
                        Long stationId,
                        List<WeeklyScheduleDTO> schedules) {

                User user = getCurrentUser();

                Long validStationId = requireId(stationId, "L'identifiant de la borne est obligatoire.");

                ChargingStation station = findOwnedStation(validStationId, user);

                if (schedules == null) {
                        throw new BusinessException("La liste des horaires est obligatoire.");
                }

                weeklyScheduleRepo.deleteByChargingStationId(validStationId);

                for (WeeklyScheduleDTO dto : schedules) {
                        validateSchedule(dto);

                        DayOfWeek dayOfWeek = dayOfWeekRepo
                                        .findById(dto.getDayOfWeekId().shortValue())
                                        .orElseThrow(() -> new ResourceNotFoundException("Jour de la semaine introuvable."));

                        WeeklySchedule weeklySchedule = new WeeklySchedule();

                        weeklySchedule.setChargingStation(station);
                        weeklySchedule.setDayOfWeek(dayOfWeek);
                        weeklySchedule.setStartTime(dto.getStartTime());
                        weeklySchedule.setEndTime(dto.getEndTime());

                        weeklyScheduleRepo.save(weeklySchedule);
                }
        }

        private User getCurrentUser() {
                return (User) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();
        }

        private Long requireId(Long id, String message) {
                if (id == null) {
                        throw new BusinessException(message);
                }

                return id;
        }

        private NicknameLocation findUserLocation(Long locationId, User user) {
                return nicknameLocationRepo
                                .findByStationLocationIdAndUser(locationId, user)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Emplacement introuvable ou inaccessible."));
        }

        private Power findPower(Long powerId) {
                return powerRepo.findById(powerId)
                                .orElseThrow(() -> new ResourceNotFoundException("Puissance introuvable."));
        }

        private StatusChargingStation findStatus(Long statusId) {
                return statusChargingStationRepo.findById(statusId)
                                .orElseThrow(() -> new ResourceNotFoundException("Statut de borne introuvable."));
        }

        private ChargingStation findOwnedStation(Long stationId, User user) {
                return stationRepo
                                .findByIdAndLocationUser(stationId, user)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Borne introuvable ou inaccessible."));
        }

        private void validateSchedule(WeeklyScheduleDTO dto) {
                if (dto == null) {
                        throw new BusinessException("Un créneau horaire est invalide.");
                }

                if (dto.getDayOfWeekId() == null) {
                        throw new BusinessException("Le jour de la semaine est obligatoire.");
                }

                if (dto.getStartTime() == null || dto.getEndTime() == null) {
                        throw new BusinessException("Les horaires de début et de fin sont obligatoires.");
                }

                validateHalfHour(dto.getStartTime());
                validateHalfHour(dto.getEndTime());

                if (!dto.getStartTime().isBefore(dto.getEndTime())) {
                        throw new BusinessException("L'heure de début doit être avant l'heure de fin.");
                }
        }

        private void validateHalfHour(LocalTime time) {
                int minute = time.getMinute();

                if (minute != 0 && minute != 30) {
                        throw new BusinessException("Les horaires doivent être par tranche de 30 minutes.");
                }
        }
}