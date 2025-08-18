package com.hexavolt.backend.service;

import com.hexavolt.backend.entity.User;

public interface JwtService {
  String generateToken(User user);
  String getSubject(String token); // email
}
