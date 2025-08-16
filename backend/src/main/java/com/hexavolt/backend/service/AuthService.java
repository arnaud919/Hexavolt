package com.hexavolt.backend.service;

import com.hexavolt.backend.dto.RegisterRequest;
import com.hexavolt.backend.dto.UserResponse;

public interface AuthService {
  UserResponse register(RegisterRequest request);
}
