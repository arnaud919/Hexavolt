package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.dto.LoginRequest;
import com.hexavolt.backend.dto.RegisterRequest;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.entity.UserToken;
import com.hexavolt.backend.repository.CityRepository;
import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.repository.UserTokenRepository;
import com.hexavolt.backend.service.AuthService;
import com.hexavolt.backend.service.JwtService;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepo;
  private final CityRepository cityRepo;
  private final UserTokenRepository tokenRepo;
  private final JwtService jwtService;
  private final PasswordEncoder encoder;

  public AuthServiceImpl(UserRepository userRepo,
      CityRepository cityRepo,
      UserTokenRepository tokenRepo,
      JwtService jwtService,
      PasswordEncoder encoder) {
    this.userRepo = userRepo;
    this.cityRepo = cityRepo;
    this.tokenRepo = tokenRepo;
    this.jwtService = jwtService;
    this.encoder = encoder;
  }

  @Override
  @Transactional
  public void register(RegisterRequest request) {

    if (userRepo.findByEmail(request.email()).isPresent()) {
      throw new IllegalArgumentException("Email already in use");
    }

    var city = cityRepo.findById(request.cityId())
        .orElseThrow(() -> new IllegalArgumentException("City not found"));

    User user = new User();
    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());
    user.setAddress(request.address());
    user.setPostalCode(request.postalCode());
    user.setPhone(request.phone());
    user.setBirthdate(request.birthdate());

    // ⚠️ city à mapper via CityRepository si besoin
    user.setCity(city);

    user.setEmail(request.email());
    user.setPassword(encoder.encode(request.password()));
    user.setEmailIsValid(false);

    userRepo.save(user);

    // Génération du token d’activation
    UserToken token = new UserToken();
    token.setToken(UUID.randomUUID().toString());
    token.setUser(user);
    token.setType(UserToken.TokenType.ACTIVATION);
    token.setExpiresAt(LocalDateTime.now().plusHours(24));
    tokenRepo.save(token);

    // TODO : envoyer email avec lien http://.../api/auth/verify?token=...
  }

  @Override
  @Transactional
  public void verifyEmail(String tokenValue) {
    UserToken token = tokenRepo.findByToken(tokenValue)
        .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

    if (token.getType() != UserToken.TokenType.ACTIVATION) {
      throw new IllegalArgumentException("Invalid token type");
    }
    if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("Token expired");
    }
    if (token.getUsedAt() != null) {
      throw new IllegalArgumentException("Token already used");
    }

    User user = token.getUser();
    user.setEmailIsValid(true);
    token.setUsedAt(LocalDateTime.now());

    userRepo.save(user);
    tokenRepo.save(token);
  }

  @Override
  public String login(LoginRequest request) {
    var email = request.email().trim().toLowerCase();
    var user = userRepo.findByEmail(email)
        .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
    if (!Boolean.TRUE.equals(user.getEmailIsValid()))
      throw new BadCredentialsException("Email not verified");
    if (!encoder.matches(request.password(), user.getPassword()))
      throw new BadCredentialsException("Invalid credentials");
    return jwtService.generateToken(user);
  }

  @Override
  @Transactional
  public void requestPasswordReset(String email) {
    User user = userRepo.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    UserToken token = new UserToken();
    token.setToken(UUID.randomUUID().toString());
    token.setUser(user);
    token.setType(UserToken.TokenType.RESET_PASSWORD);
    token.setExpiresAt(LocalDateTime.now().plusHours(2));
    tokenRepo.save(token);

    // TODO : envoyer email avec lien http://.../api/auth/reset?token=...
  }

  @Override
  @Transactional
  public void resetPassword(String tokenValue, String newPassword) {
    UserToken token = tokenRepo.findByToken(tokenValue)
        .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

    if (token.getType() != UserToken.TokenType.RESET_PASSWORD) {
      throw new IllegalArgumentException("Invalid token type");
    }
    if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("Token expired");
    }
    if (token.getUsedAt() != null) {
      throw new IllegalArgumentException("Token already used");
    }

    User user = token.getUser();
    user.setPassword(encoder.encode(newPassword));
    token.setUsedAt(LocalDateTime.now());

    userRepo.save(user);
    tokenRepo.save(token);
  }
}
