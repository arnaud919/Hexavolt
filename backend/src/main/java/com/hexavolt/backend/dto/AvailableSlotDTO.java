
package com.hexavolt.backend.dto;

import java.time.LocalTime;

public class AvailableSlotDTO {

    private LocalTime startTime;
    private LocalTime endTime;

    public AvailableSlotDTO(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}

