package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.dto.ChargingStationCreateDTO;
import com.hexavolt.backend.entity.ChargingStation;
import com.hexavolt.backend.entity.NicknameLocation;
import com.hexavolt.backend.entity.Power;
import com.hexavolt.backend.entity.StationLocation;
import com.hexavolt.backend.entity.StatusChargingStation;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.repository.ChargingStationRepository;
import com.hexavolt.backend.repository.DayOfWeekRepository;
import com.hexavolt.backend.repository.NicknameLocationRepository;
import com.hexavolt.backend.repository.PowerRepository;
import com.hexavolt.backend.repository.StatusChargingStationRepository;
import com.hexavolt.backend.repository.WeeklyScheduleRepository;
import com.hexavolt.backend.service.FileStorageService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChargingStationServiceImplTest {

    @Mock
    private ChargingStationRepository stationRepo;

    @Mock
    private NicknameLocationRepository nicknameLocationRepo;

    @Mock
    private PowerRepository powerRepo;

    @Mock
    private DayOfWeekRepository dayOfWeekRepo;

    @Mock
    private WeeklyScheduleRepository weeklyScheduleRepo;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private StatusChargingStationRepository statusChargingStationRepo;

    @InjectMocks
    private ChargingStationServiceImpl chargingStationService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createShouldCreateChargingStationWhenDataIsValid() {
        User connectedUser = createConnectedUser();
        mockAuthenticatedUser(connectedUser);

        ChargingStationCreateDTO dto = createValidDto();

        StationLocation stationLocation = new StationLocation();
        stationLocation.setId(1L);
        stationLocation.setAddress("12 rue des Lilas");

        NicknameLocation nicknameLocation = new NicknameLocation();
        nicknameLocation.setStationLocation(stationLocation);
        nicknameLocation.setUser(connectedUser);
        nicknameLocation.setNickname("Maison");

        Power power = new Power();
        power.setId(1L);
        power.setKvaPower(new BigDecimal("7.4"));

        StatusChargingStation status = new StatusChargingStation();
        status.setId(1L);
        status.setName("ACTIVE");

        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "borne.jpg",
                "image/jpeg",
                "fake-photo-content".getBytes());

        MockMultipartFile video = new MockMultipartFile(
                "video",
                "borne.mp4",
                "video/mp4",
                "fake-video-content".getBytes());

        when(nicknameLocationRepo.findByStationLocationIdAndUser(1L, connectedUser))
                .thenReturn(Optional.of(nicknameLocation));

        when(powerRepo.findById(1L))
                .thenReturn(Optional.of(power));

        when(statusChargingStationRepo.findById(1L))
                .thenReturn(Optional.of(status));

        when(fileStorageService.storeChargingStationPhoto(photo))
                .thenReturn("photo-stored.jpg");

        when(fileStorageService.storeChargingStationVideo(video))
                .thenReturn("video-stored.mp4");

        chargingStationService.create(dto, photo, video);

        ArgumentCaptor<ChargingStation> stationCaptor = ArgumentCaptor.forClass(ChargingStation.class);

        verify(stationRepo).save(stationCaptor.capture());

        ChargingStation savedStation = stationCaptor.getValue();

        assertEquals("Borne maison", savedStation.getName());
        assertEquals(new BigDecimal("2.50"), savedStation.getHourlyRate());
        assertEquals("Se garer devant le portail.", savedStation.getInstruction());
        assertEquals(Boolean.FALSE, savedStation.getIsCustom());
        assertEquals(power, savedStation.getPower());
        assertEquals(status, savedStation.getStatus());
        assertEquals(stationLocation, savedStation.getLocation());
        assertEquals(48.8566, savedStation.getLatitude(), 0.000001);
        assertEquals(2.3522, savedStation.getLongitude(), 0.000001);
        assertEquals("photo-stored.jpg", savedStation.getPhotoName());
        assertEquals("video-stored.mp4", savedStation.getVideoName());

        verify(fileStorageService).storeChargingStationPhoto(photo);
        verify(fileStorageService).storeChargingStationVideo(video);
    }

    private User createConnectedUser() {
        User user = new User();
        user.setId(10L);
        user.setEmail("test@email.com");
        return user;
    }

    private void mockAuthenticatedUser(User user) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private ChargingStationCreateDTO createValidDto() {
        ChargingStationCreateDTO dto = new ChargingStationCreateDTO();

        dto.setLocationId(1L);
        dto.setPowerId(1L);
        dto.setStatusId(1L);
        dto.setName("Borne maison");
        dto.setHourlyRate(new BigDecimal("2.50"));
        dto.setInstruction("Se garer devant le portail.");
        dto.setCustom(false);
        dto.setLatitude(48.8566);
        dto.setLongitude(2.3522);

        return dto;
    }
}