package com.hexavolt.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  List<Reservation> findByStatusNameAndStartDateTimeBefore(
      String statusName,
      LocalDateTime dateTime);

}
