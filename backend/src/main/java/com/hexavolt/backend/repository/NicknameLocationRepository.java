package com.hexavolt.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.NicknameLocation;

public interface NicknameLocationRepository extends JpaRepository<NicknameLocation, Integer> {
  // par lieu (stationLocation)
  List<NicknameLocation> findByStationLocation_Id(Integer stationLocationId);

  // par utilisateur
  List<NicknameLocation> findByUser_Id(Integer userId);

  // un enregistrement précis (utile avec ta contrainte UNIQUE (user_id, station_location))
  Optional<NicknameLocation> findByUser_IdAndStationLocation_Id(Integer userId, Integer stationLocationId);

  // contrôles d’existence (pour éviter les doublons côté service)
  boolean existsByUser_IdAndStationLocation_Id(Integer userId, Integer stationLocationId);

  boolean existsByUser_IdAndNicknameIgnoreCaseAndStationLocation_Id(Integer userId,
      String nickname,
      Integer stationLocationId);
}
