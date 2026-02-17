package com.hexavolt.backend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hexavolt.backend.entity.ChargingStation;
import com.hexavolt.backend.entity.DayOfWeek;
import com.hexavolt.backend.entity.ModifiedSchedule;
import com.hexavolt.backend.entity.WeeklySchedule;
import com.hexavolt.backend.repository.ClosedDayRepository;
import com.hexavolt.backend.repository.DayOfWeekRepository;
import com.hexavolt.backend.repository.ModifiedScheduleRepository;
import com.hexavolt.backend.repository.WeeklyScheduleRepository;
import com.hexavolt.backend.service.ChargingStationAvailabilityService;

@Service
@Transactional(readOnly = true)
public class ChargingStationAvailabilityServiceImpl
                implements ChargingStationAvailabilityService {

        private final ClosedDayRepository closedDayRepository;
        private final ModifiedScheduleRepository modifiedScheduleRepository;
        private final WeeklyScheduleRepository weeklyScheduleRepository;
        private final DayOfWeekRepository dayOfWeekRepository;

        public ChargingStationAvailabilityServiceImpl(
                        ClosedDayRepository closedDayRepository,
                        ModifiedScheduleRepository modifiedScheduleRepository,
                        WeeklyScheduleRepository weeklyScheduleRepository,
                        DayOfWeekRepository dayOfWeekRepository) {
                this.closedDayRepository = closedDayRepository;
                this.modifiedScheduleRepository = modifiedScheduleRepository;
                this.weeklyScheduleRepository = weeklyScheduleRepository;
                this.dayOfWeekRepository = dayOfWeekRepository;
        }

        @Override
        public boolean isAvailable(
                        ChargingStation station,
                        LocalDateTime start,
                        LocalDateTime end) {

                LocalDate date = start.toLocalDate();

                // 🔴 1️⃣ Jour fermé (priorité absolue)
                boolean closed = closedDayRepository
                                .existsByChargingStationIdAndDate(station.getId(), date);

                if (closed) {
                        return false;
                }

                // 🟨 2️⃣ Horaire modifié prioritaire
                ModifiedSchedule modified = modifiedScheduleRepository
                                .findByChargingStationIdAndDate(station.getId(), date)
                                .orElse(null);

                if (modified != null) {
                        return isInside(
                                        start.toLocalTime(),
                                        end.toLocalTime(),
                                        modified.getStartTime(),
                                        modified.getEndTime());
                }

                // 🟦 3️⃣ Horaire hebdomadaire
                short dayId = (short) start.getDayOfWeek().getValue(); // 1..7

                DayOfWeek dayOfWeek = dayOfWeekRepository
                                .findById(dayId)
                                .orElse(null);

                if (dayOfWeek == null) {
                        return false;
                }

                WeeklySchedule weekly = weeklyScheduleRepository
                                .findByChargingStationIdAndDayOfWeek(
                                                station.getId(),
                                                dayOfWeek)
                                .orElse(null);

                if (weekly == null) {
                        return false;
                }

                return isInside(
                                start.toLocalTime(),
                                end.toLocalTime(),
                                weekly.getStartTime(),
                                weekly.getEndTime());
        }

        private boolean isInside(
                        LocalTime requestedStart,
                        LocalTime requestedEnd,
                        LocalTime allowedStart,
                        LocalTime allowedEnd) {
                return !requestedStart.isBefore(allowedStart)
                                && !requestedEnd.isAfter(allowedEnd);
        }
}