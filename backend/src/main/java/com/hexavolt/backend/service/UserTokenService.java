package com.hexavolt.backend.service;

import java.time.Duration;

import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.entity.UserToken;

public interface UserTokenService {
  UserToken createActivationToken(User user, Duration ttl);
  UserToken createResetPasswordToken(User user, Duration ttl);

  UserToken validateActivationToken(String rawToken);
  UserToken validateResetPasswordToken(String rawToken);

  void consume(UserToken token);
  void invalidateAllResetTokens(Integer userId);
}