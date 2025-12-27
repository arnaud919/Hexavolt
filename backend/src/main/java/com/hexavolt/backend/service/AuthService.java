package com.hexavolt.backend.service;

import com.hexavolt.backend.dto.LoginRequest;
import com.hexavolt.backend.dto.RegisterRequest;
import com.hexavolt.backend.dto.ResetPasswordConfirm;

public interface AuthService {
  void register(RegisterRequest request);

  void verifyEmail(String token);

  void resendVerificationEmail(String email);

  String login(LoginRequest request); // retourne le JWT

  void requestPasswordReset(String email);

  void resetPassword(String token, String newPassword);

  void confirmPasswordReset(ResetPasswordConfirm dto);
}
