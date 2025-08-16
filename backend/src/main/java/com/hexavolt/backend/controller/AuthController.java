package com.hexavolt.backend.controller;

import com.hexavolt.backend.dto.RegisterRequest;
import com.hexavolt.backend.dto.UserResponse;
import com.hexavolt.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  public AuthController(AuthService authService) { this.authService = authService; }

  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
    var created = authService.register(request);
    return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
  }
}
