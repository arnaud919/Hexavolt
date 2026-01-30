package com.hexavolt.backend.repository;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hexavolt.backend.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  // @Query("""
  //   select (count(r)>0) from Reservation r
  //   where r.station.id = :stationId
  //     and r.status in ('PENDING','CONFIRMED')
  //     and r.startTime < :end and r.endTime > :start
  // """)
  // boolean existsOverlap(@Param("stationId") Integer stationId,
  //                       @Param("start") OffsetDateTime start,
  //                       @Param("end") OffsetDateTime end);

  // Page<Reservation> findByRenter_IdOrderByStartTimeDesc(Integer renterId, Pageable pageable);
  // Page<Reservation> findByStation_IdOrderByStartTimeDesc(Integer stationId, Pageable pageable);
}

