package com.hexavolt.backend.dto;

import com.hexavolt.backend.validation.StrongPassword;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(
    @NotBlank String currentPassword,
    @NotBlank @StrongPassword String newPassword
) {}
