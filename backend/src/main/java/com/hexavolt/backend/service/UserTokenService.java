package com.hexavolt.backend.service;

import java.time.Duration;

import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.entity.UserToken;

public interface UserTokenService {
  UserToken createActivationToken(User user, Duration ttl);
  UserToken createResetToken(User user, Duration ttl);
  UserToken consumeActivationToken(String token);
  UserToken consumeResetToken(String token);
}