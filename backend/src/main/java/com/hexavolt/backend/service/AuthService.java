package com.hexavolt.backend.service;

import com.hexavolt.backend.dto.LoginRequestDTO;
import com.hexavolt.backend.dto.ProfileDTO;
import com.hexavolt.backend.dto.RegisterRequestDTO;
import com.hexavolt.backend.dto.ResetPasswordConfirmDTO;

public interface AuthService {
  void register(RegisterRequestDTO request);

  void verifyEmail(String token);

  void resendVerificationEmail(String email);

  String login(LoginRequestDTO request); // retourne le JWT

  void requestPasswordReset(String email);

  void resetPassword(String token, String newPassword);

  void confirmPasswordReset(ResetPasswordConfirmDTO dto);

  ProfileDTO getProfile();

}
