
package com.hexavolt.backend.dto;

import java.time.LocalDate;

public class ClosedDayDTO {

    private LocalDate date;

    public ClosedDayDTO() {
    }

    public ClosedDayDTO(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

