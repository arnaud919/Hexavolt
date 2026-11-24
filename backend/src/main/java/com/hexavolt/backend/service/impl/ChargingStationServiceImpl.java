package com.hexavolt.backend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hexavolt.backend.dto.ChargingStationCreateDTO;
import com.hexavolt.backend.dto.ChargingStationDetailDTO;
import com.hexavolt.backend.dto.ChargingStationEditDTO;
import com.hexavolt.backend.dto.ChargingStationListDTO;
import com.hexavolt.backend.dto.ChargingStationPublicDetailDTO;
import com.hexavolt.backend.dto.ChargingStationSearchDTO;
import com.hexavolt.backend.dto.ChargingStationUpdateDTO;
import com.hexavolt.backend.dto.ClosedDayDTO;
import com.hexavolt.backend.dto.ModifiedScheduleDTO;
import com.hexavolt.backend.dto.WeeklyScheduleDTO;
import com.hexavolt.backend.entity.ChargingStation;
import com.hexavolt.backend.entity.ClosedDay;
import com.hexavolt.backend.entity.DayOfWeek;
import com.hexavolt.backend.entity.ModifiedSchedule;
import com.hexavolt.backend.entity.NicknameLocation;
import com.hexavolt.backend.entity.Power;
import com.hexavolt.backend.entity.StationLocation;
import com.hexavolt.backend.entity.StatusChargingStation;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.entity.WeeklySchedule;
import com.hexavolt.backend.exception.BusinessException;
import com.hexavolt.backend.exception.ResourceNotFoundException;
import com.hexavolt.backend.repository.ChargingStationRepository;
import com.hexavolt.backend.repository.ClosedDayRepository;
import com.hexavolt.backend.repository.ChargingStationReservationRepository;
import com.hexavolt.backend.repository.DayOfWeekRepository;
import com.hexavolt.backend.repository.ModifiedScheduleRepository;
import com.hexavolt.backend.repository.NicknameLocationRepository;
import com.hexavolt.backend.repository.PowerRepository;
import com.hexavolt.backend.repository.StatusChargingStationRepository;
import com.hexavolt.backend.repository.WeeklyScheduleRepository;
import com.hexavolt.backend.service.ChargingStationService;
import com.hexavolt.backend.service.FileStorageService;

import com.hexavolt.backend.mapper.ChargingStationMapper;

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
        private final ChargingStationMapper chargingStationMapper;
        private final ClosedDayRepository closedDayRepo;
        private final ChargingStationReservationRepository chargingStationReservationRepo;
        private final ModifiedScheduleRepository modifiedScheduleRepo;

        public ChargingStationServiceImpl(
                        ChargingStationRepository stationRepo,
                        NicknameLocationRepository nicknameLocationRepo,
                        PowerRepository powerRepo,
                        DayOfWeekRepository dayOfWeekRepo,
                        WeeklyScheduleRepository weeklyScheduleRepo,
                        FileStorageService fileStorageService,
                        StatusChargingStationRepository statusChargingStationRepo,
                        ChargingStationMapper chargingStationMapper,
                        ClosedDayRepository closedDayRepo,
                        ChargingStationReservationRepository chargingStationReservationRepo,
                        ModifiedScheduleRepository modifiedScheduleRepo) {
                this.stationRepo = stationRepo;
                this.nicknameLocationRepo = nicknameLocationRepo;
                this.powerRepo = powerRepo;
                this.dayOfWeekRepo = dayOfWeekRepo;
                this.weeklyScheduleRepo = weeklyScheduleRepo;
                this.fileStorageService = fileStorageService;
                this.statusChargingStationRepo = statusChargingStationRepo;
                this.chargingStationMapper = chargingStationMapper;
                this.closedDayRepo = closedDayRepo;
                this.chargingStationReservationRepo = chargingStationReservationRepo;
                this.modifiedScheduleRepo = modifiedScheduleRepo;

        }

        private static final double MAX_DISTANCE_FROM_LOCATION_METERS = 300.0;
        private static final String STATUS_STATION_WITHDRAWN = "RETIRE";

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
                validateStatusNotWithdrawn(status);

                validateStationCloseToLocation(
                                nicknameLocation.getStationLocation(),
                                dto.getLatitude(),
                                dto.getLongitude());

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

                List<WeeklyScheduleDTO> weeklySchedules = getWeeklySchedules(station.getId());

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
                withdrawMyChargingStation(id);
        }

        @Override
        public void updateWeeklySchedule(
                        Long stationId,
                        List<WeeklyScheduleDTO> schedules) {

                User user = getCurrentUser();
                Long validStationId = requireId(stationId, "L'identifiant de la borne est obligatoire.");
                ChargingStation station = findOwnedStation(validStationId, user);

                validateStationNotWithdrawn(station);

                long conflicts = chargingStationReservationRepo
                                .countBlockingReservationConflict(
                                                station.getId(),
                                                LocalDateTime.now(),
                                                LocalDateTime.of(9999, 12, 31, 23, 59, 59));

                if (conflicts > 0) {
                        throw new BusinessException(
                                        "Impossible de modifier les horaires hebdomadaires : "
                                                        + "cette borne possède des réservations à venir "
                                                        + "en attente ou confirmées.");
                }

                if (schedules == null) {
                        throw new BusinessException("La liste des horaires est obligatoire.");
                }

                weeklyScheduleRepo.deleteByChargingStationId(validStationId);

                for (WeeklyScheduleDTO dto : schedules) {
                        validateSchedule(dto);

                        DayOfWeek dayOfWeek = dayOfWeekRepo
                                        .findById(dto.getDayOfWeekId().shortValue())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Jour de la semaine introuvable."));

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

        @Override
        public void updateMyChargingStation(Long id, ChargingStationUpdateDTO dto) {
                if (dto == null) {
                        throw new BusinessException(
                                        "Les informations de la borne sont obligatoires.");
                }

                User user = getCurrentUser();

                Long stationId = requireId(
                                id,
                                "L'identifiant de la borne est obligatoire.");

                Long powerId = requireId(
                                dto.getPowerId(),
                                "La puissance est obligatoire.");

                Long statusId = requireId(
                                dto.getStatusId(),
                                "Le statut est obligatoire.");

                ChargingStation station = findOwnedStation(stationId, user);

                validateStationNotWithdrawn(station);

                Power power = findPower(powerId);
                StatusChargingStation status = findStatus(statusId);

                validateStatusNotWithdrawn(status);

                validateStationCloseToLocation(
                                station.getLocation(),
                                dto.getLatitude(),
                                dto.getLongitude());

                station.setName(dto.getName());
                station.setHourlyRate(dto.getHourlyRate());
                station.setInstruction(dto.getInstruction());
                station.setIsCustom(dto.isCustom());
                station.setLatitude(dto.getLatitude());
                station.setLongitude(dto.getLongitude());
                station.setPower(power);
                station.setStatus(status);

                stationRepo.save(station);
        }

        @Override
        public ChargingStationEditDTO findMyChargingStationForEdit(Long id) {
                User user = getCurrentUser();

                Long stationId = requireId(id, "L'identifiant de la borne est obligatoire.");

                ChargingStation station = findOwnedStation(stationId, user);

                validateStationNotWithdrawn(station);

                return new ChargingStationEditDTO(
                                station.getId(),
                                station.getName(),
                                station.getPower().getId(),
                                station.getHourlyRate(),
                                station.getInstruction(),
                                station.getIsCustom(),
                                station.getLatitude(),
                                station.getLongitude(),
                                station.getStatus() != null ? station.getStatus().getId() : null);
        }

        @Override
        public List<ChargingStationSearchDTO> searchActiveStations() {
                return stationRepo.findActiveStationsForSearch()
                                .stream()
                                .map(chargingStationMapper::toSearchDTO)
                                .toList();
        }

        private void validateStationCloseToLocation(
                        StationLocation location,
                        Double stationLatitude,
                        Double stationLongitude) {
                if (stationLatitude == null || stationLongitude == null) {
                        throw new BusinessException("La position de la borne est obligatoire.");
                }

                if (location.getLatitude() == null || location.getLongitude() == null) {
                        throw new BusinessException("La position du lieu est obligatoire.");
                }

                double distance = calculateDistanceInMeters(
                                location.getLatitude(),
                                location.getLongitude(),
                                stationLatitude,
                                stationLongitude);

                if (distance > MAX_DISTANCE_FROM_LOCATION_METERS) {
                        throw new BusinessException(
                                        "La borne doit être placée à proximité de l'adresse du lieu.");
                }
        }

        private double calculateDistanceInMeters(
                        double latitude1,
                        double longitude1,
                        double latitude2,
                        double longitude2) {
                final double earthRadiusMeters = 6371000;

                double lat1Rad = Math.toRadians(latitude1);
                double lat2Rad = Math.toRadians(latitude2);

                double deltaLat = Math.toRadians(latitude2 - latitude1);
                double deltaLon = Math.toRadians(longitude2 - longitude1);

                double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                                                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

                return earthRadiusMeters * c;
        }

        @Override
        public void withdrawMyChargingStation(Long id) {
                User user = getCurrentUser();

                Long stationId = requireId(
                                id,
                                "L'identifiant de la borne est obligatoire.");

                ChargingStation station = findOwnedStation(stationId, user);

                validateStationNotWithdrawn(station);

                StatusChargingStation withdrawnStatus = statusChargingStationRepo
                                .findByName(STATUS_STATION_WITHDRAWN)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Statut de borne introuvable."));

                station.setStatus(withdrawnStatus);

                stationRepo.save(station);
        }

        private void validateStationNotWithdrawn(ChargingStation station) {
                if (station.getStatus() != null
                                && STATUS_STATION_WITHDRAWN.equals(station.getStatus().getName())) {
                        throw new BusinessException(
                                        "Cette borne a été retirée définitivement et ne peut plus être modifiée.");
                }
        }

        private void validateStatusNotWithdrawn(StatusChargingStation status) {
                if (STATUS_STATION_WITHDRAWN.equals(status.getName())) {
                        throw new BusinessException(
                                        "Le retrait définitif doit être effectué via l'action dédiée.");
                }
        }

        @Override
        public ChargingStationPublicDetailDTO findPublicChargingStationById(Long id) {

                Long stationId = requireId(
                                id,
                                "L'identifiant de la borne est obligatoire.");

                ChargingStation station = stationRepo
                                .findByIdAndStatusName(stationId, "ACTIVE")
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Borne introuvable ou indisponible."));

                String photoUrl = station.getPhotoName() != null
                                ? "/api/public/stations/" + station.getId() + "/photo"
                                : null;

                String videoUrl = station.getVideoName() != null
                                ? "/api/public/stations/" + station.getId() + "/video"
                                : null;

                List<WeeklyScheduleDTO> weeklySchedules = getWeeklySchedules(station.getId());

                return new ChargingStationPublicDetailDTO(
                                station.getId(),
                                station.getName(),
                                station.getPower().getKvaPower(),
                                station.getHourlyRate(),
                                station.getInstruction(),
                                station.getLatitude(),
                                station.getLongitude(),
                                station.getLocation().getAddress(),
                                station.getLocation().getCity().getName(),
                                photoUrl,
                                videoUrl,
                                weeklySchedules);
        }

        private List<WeeklyScheduleDTO> getWeeklySchedules(Long stationId) {

                return weeklyScheduleRepo
                                .findByChargingStationId(stationId)
                                .stream()
                                .map(schedule -> {

                                        WeeklyScheduleDTO dto = new WeeklyScheduleDTO();

                                        dto.setDayOfWeekId(
                                                        schedule.getDayOfWeek().getId().longValue());

                                        dto.setStartTime(schedule.getStartTime());
                                        dto.setEndTime(schedule.getEndTime());

                                        return dto;
                                })
                                .toList();
        }

        @Override
        public List<ClosedDayDTO> getClosedDays(Long stationId) {
                User user = getCurrentUser();
                ChargingStation station = findOwnedStation(stationId, user);

                return closedDayRepo
                                .findByChargingStationIdOrderByDateAsc(station.getId())
                                .stream()
                                .map(closedDay -> new ClosedDayDTO(closedDay.getDate()))
                                .toList();
        }

        @Override
        public void addClosedDay(Long stationId, ClosedDayDTO dto) {
                if (dto == null || dto.getDate() == null) {
                        throw new BusinessException("La date de fermeture est obligatoire.");
                }

                LocalDate date = dto.getDate();

                if (date.isBefore(LocalDate.now())) {
                        throw new BusinessException(
                                        "La date de fermeture ne peut pas être dans le passé.");
                }

                User user = getCurrentUser();
                ChargingStation station = findOwnedStation(stationId, user);

                if (closedDayRepo.existsByChargingStationIdAndDate(
                                station.getId(), date)) {
                        throw new BusinessException(
                                        "Cette journée est déjà enregistrée comme fermée.");
                }

                long conflicts = chargingStationReservationRepo
                                .countBlockingReservationConflict(
                                                station.getId(),
                                                date.atStartOfDay(),
                                                date.plusDays(1).atStartOfDay());

                if (conflicts > 0) {
                        throw new BusinessException(
                                        "Cette journée possède des réservations en attente ou confirmées.");
                }

                ClosedDay closedDay = new ClosedDay();
                closedDay.setChargingStation(station);
                closedDay.setDate(date);

                closedDayRepo.save(closedDay);
        }

        @Override
        public void deleteClosedDay(Long stationId, LocalDate date) {
                User user = getCurrentUser();
                ChargingStation station = findOwnedStation(stationId, user);

                ClosedDay closedDay = closedDayRepo
                                .findByChargingStationIdAndDate(station.getId(), date)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Journée de fermeture introuvable."));

                closedDayRepo.delete(closedDay);
        }

        @Override
        public List<ModifiedScheduleDTO> getModifiedSchedules(Long stationId) {
                User user = getCurrentUser();
                ChargingStation station = findOwnedStation(stationId, user);

                return modifiedScheduleRepo
                                .findByChargingStationIdOrderByDateAsc(station.getId())
                                .stream()
                                .map(schedule -> new ModifiedScheduleDTO(
                                                schedule.getDate(),
                                                schedule.getStartTime(),
                                                schedule.getEndTime()))
                                .toList();
        }

        @Override
        public void saveModifiedSchedule(
                        Long stationId,
                        ModifiedScheduleDTO dto) {

                if (dto == null || dto.getDate() == null
                                || dto.getStartTime() == null
                                || dto.getEndTime() == null) {
                        throw new BusinessException(
                                        "La date et les horaires sont obligatoires.");
                }

                if (dto.getDate().isBefore(LocalDate.now())) {
                        throw new BusinessException(
                                        "La date ne peut pas être dans le passé.");
                }

                if (!isValidHalfHour(dto.getStartTime())
                                || !isValidHalfHour(dto.getEndTime())) {
                        throw new BusinessException(
                                        "Les horaires doivent être par tranches de 30 minutes.");
                }

                if (!dto.getStartTime().isBefore(dto.getEndTime())) {
                        throw new BusinessException(
                                        "L'heure de début doit être avant l'heure de fin.");
                }

                User user = getCurrentUser();
                ChargingStation station = findOwnedStation(stationId, user);

                if (closedDayRepo.existsByChargingStationIdAndDate(
                                station.getId(), dto.getDate())) {
                        throw new BusinessException(
                                        "Cette journée est déjà enregistrée comme fermée.");
                }

                long conflicts = chargingStationReservationRepo
                                .countBlockingReservationConflict(
                                                station.getId(),
                                                dto.getDate().atStartOfDay(),
                                                dto.getDate().plusDays(1).atStartOfDay());

                if (conflicts > 0) {
                        throw new BusinessException(
                                        "Cette journée possède des réservations en attente ou confirmées.");
                }

                ModifiedSchedule schedule = modifiedScheduleRepo
                                .findByChargingStationIdAndDate(station.getId(), dto.getDate())
                                .orElseGet(() -> {
                                        ModifiedSchedule newSchedule = new ModifiedSchedule();
                                        newSchedule.setChargingStation(station);
                                        newSchedule.setDate(dto.getDate());
                                        return newSchedule;
                                });

                schedule.setStartTime(dto.getStartTime());
                schedule.setEndTime(dto.getEndTime());

                modifiedScheduleRepo.save(schedule);
        }

        @Override
        public void deleteModifiedSchedule(Long stationId, LocalDate date) {
                User user = getCurrentUser();
                ChargingStation station = findOwnedStation(stationId, user);

                ModifiedSchedule schedule = modifiedScheduleRepo
                                .findByChargingStationIdAndDate(station.getId(), date)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Horaire exceptionnel introuvable."));

                long conflicts = chargingStationReservationRepo
                                .countBlockingReservationConflict(
                                                station.getId(),
                                                date.atStartOfDay(),
                                                date.plusDays(1).atStartOfDay());

                if (conflicts > 0) {
                        throw new BusinessException(
                                        "Impossible de supprimer cet horaire exceptionnel : "
                                                        + "cette journée possède des réservations "
                                                        + "en attente ou confirmées.");
                }

                modifiedScheduleRepo.delete(schedule);
        }

        private boolean isValidHalfHour(LocalTime time) {
                return (time.getMinute() == 0 || time.getMinute() == 30)
                                && time.getSecond() == 0
                                && time.getNano() == 0;
        }

}