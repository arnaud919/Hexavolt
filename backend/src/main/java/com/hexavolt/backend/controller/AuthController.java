package com.hexavolt.backend.controller;

import com.hexavolt.backend.dto.LoginRequestDTO;
import com.hexavolt.backend.dto.ProfileDTO;
import com.hexavolt.backend.dto.RegisterRequestDTO;
import com.hexavolt.backend.dto.ResendActivationRequestDTO;
import com.hexavolt.backend.dto.UserMeDTO;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.time.Duration;
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
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDTO req) {
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
    public ResponseEntity<Void> resendVerification(@Valid @RequestBody ResendActivationRequestDTO req) {
        authService.resendVerificationEmail(req.email());
        return ResponseEntity.noContent().build(); // ou ok(), mais 204 est cohérent avec verify()
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        String token = authService.login(request); // ta méthode renvoie le token

        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // ❗️Seulement si HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) Duration.ofHours(2).getSeconds());
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("access_token", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileDTO> me() {
        return ResponseEntity.ok(authService.getProfile());
    }
}
