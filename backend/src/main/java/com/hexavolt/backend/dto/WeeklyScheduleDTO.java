package com.hexavolt.backend.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class WeeklyScheduleDTO {

    private Long dayOfWeekId;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    public WeeklyScheduleDTO() {
    }

    public Long getDayOfWeekId() {
        return dayOfWeekId;
    }

    public void setDayOfWeekId(Long dayOfWeekId) {
        this.dayOfWeekId = dayOfWeekId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}