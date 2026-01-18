package com.hexavolt.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendActivationRequestDTO(
        @NotBlank @Email String email
) {}
