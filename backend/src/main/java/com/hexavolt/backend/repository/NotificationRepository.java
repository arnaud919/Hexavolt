package com.hexavolt.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexavolt.backend.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  // List<Notification> findTop50ByUser_IdAndReadFalseOrderByCreatedAtDesc(Integer userId);
}

