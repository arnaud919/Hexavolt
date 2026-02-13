package com.hexavolt.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hexavolt.backend.entity.ChargingStationReservation;

public interface ChargingStationReservationRepository
                extends JpaRepository<ChargingStationReservation, Long> {

        @Query("""
                        SELECT COUNT(csr)
                        FROM ChargingStationReservation csr
                        JOIN csr.reservation r
                        WHERE csr.chargingStation.id = :stationId
                        AND r.status.name = 'CONFIRMEE'
                        AND :start < r.endDateTime
                        AND :end > r.startDateTime
                """)
        long countConfirmedReservationConflict(
                        @Param("stationId") Long stationId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        List<ChargingStationReservation> findByReservationUserIdOrderByReservationStartDateTimeDesc(Long userId);

        List<ChargingStationReservation> findByChargingStationLocationUserIdAndReservationStatusNameOrderByReservationStartDateTimeAsc(
                        Long ownerId,
                        String statusName);

        Optional<ChargingStationReservation> findByReservationId(Long reservationId);

}
