package com.hexavolt.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.NicknameLocation;
import com.hexavolt.backend.entity.User;

public interface NicknameLocationRepository extends JpaRepository<NicknameLocation, Long> {
  // par lieu (stationLocation)
  List<NicknameLocation> findByStationLocation_Id(Long stationLocationId);

  // par utilisateur
  List<NicknameLocation> findByUser(User user);

  // un enregistrement précis (utile avec ta contrainte UNIQUE (user_id,
  // station_location))
  Optional<NicknameLocation> findByStationLocationIdAndUser(
      Long locationId,
      User user);

  // contrôles d’existence (pour éviter les doublons côté service)
  boolean existsByUser_IdAndStationLocation_Id(Long userId, Long stationLocationId);

  boolean existsByUser_IdAndNicknameIgnoreCaseAndStationLocation_Id(Long userId,
      String nickname,
      Long stationLocationId);
}
