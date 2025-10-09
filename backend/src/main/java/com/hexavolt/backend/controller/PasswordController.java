package com.hexavolt.backend.controller;

import com.hexavolt.backend.dto.ResetPasswordConfirm;
import com.hexavolt.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/password")
public class PasswordController {

    private final AuthService authService = null;

    // 1) Demande de reset (toujours 200 pour éviter l’énumération d’emails)
    @PostMapping("/reset/request")
    public ResponseEntity<Void> requestReset(@RequestBody String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }

    // 2) Confirmation du reset avec le token et le nouveau mot de passe
    @PostMapping("/reset/confirm")
    public ResponseEntity<Void> confirmReset(@RequestBody ResetPasswordConfirm dto) {
        authService.confirmPasswordReset(dto);
        return ResponseEntity.ok().build();
    }
}
