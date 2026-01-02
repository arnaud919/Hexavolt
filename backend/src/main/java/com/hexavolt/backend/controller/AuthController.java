package com.hexavolt.backend.controller;

import com.hexavolt.backend.dto.LoginRequest;
import com.hexavolt.backend.dto.RegisterRequest;
import com.hexavolt.backend.dto.ResendActivationRequest;
import com.hexavolt.backend.dto.UserMe;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.service.AuthService;

import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepo;

    public AuthController(AuthService authService, UserRepository userRepo) {
        this.authService = authService;
        this.userRepo = userRepo;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        Map<String, String> response = Map.of(
                "message", "Inscription réussie. Veuillez confirmer votre email pour activer votre compte.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verify(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify/resend")
    public ResponseEntity<Void> resendVerification(@Valid @RequestBody ResendActivationRequest req) {
        authService.resendVerificationEmail(req.email());
        return ResponseEntity.noContent().build(); // ou ok(), mais 204 est cohérent avec verify()
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/reset/request")
    public ResponseEntity<Void> requestReset(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset/confirm")
    public ResponseEntity<Void> resetPassword(@RequestParam String token, @RequestParam String password) {
        authService.resetPassword(token, password);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserMe> me(Authentication auth) {
        String email = auth.getName(); // subject
        User user = userRepo.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(new UserMe(user.getId(), user.getEmail()));
    }
}
