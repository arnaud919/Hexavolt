
package com.hexavolt.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class ChargingStationPublicDetailDTO {

    private Long id;
    private String name;
    private BigDecimal power;
    private BigDecimal hourlyRate;
    private String instruction;
    private Double latitude;
    private Double longitude;
    private String locationAddress;
    private String cityName;
    private String photoUrl;
    private String videoUrl;
    private List<WeeklyScheduleDTO> weeklySchedules;

    public ChargingStationPublicDetailDTO(
            Long id,
            String name,
            BigDecimal power,
            BigDecimal hourlyRate,
            String instruction,
            Double latitude,
            Double longitude,
            String locationAddress,
            String cityName,
            String photoUrl,
            String videoUrl,
            List<WeeklyScheduleDTO> weeklySchedules) {

        this.id = id;
        this.name = name;
        this.power = power;
        this.hourlyRate = hourlyRate;
        this.instruction = instruction;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationAddress = locationAddress;
        this.cityName = cityName;
        this.photoUrl = photoUrl;
        this.videoUrl = videoUrl;
        this.weeklySchedules = weeklySchedules;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPower() {
        return power;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public String getInstruction() {
        return instruction;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public String getCityName() {
        return cityName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public List<WeeklyScheduleDTO> getWeeklySchedules() {
        return weeklySchedules;
    }
}
