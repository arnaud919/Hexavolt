package com.hexavolt.backend.repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hexavolt.backend.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  List<Reservation> findByStatusNameAndStartDateTimeBefore(
      String statusName,
      LocalDateTime dateTime);

}
