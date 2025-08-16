package com.hexavolt.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
  // List<Notification> findTop50ByUser_IdAndReadFalseOrderByCreatedAtDesc(Integer userId);
}

