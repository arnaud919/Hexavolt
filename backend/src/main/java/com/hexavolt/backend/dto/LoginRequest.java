// src/main/java/com/hexavolt/backend/dto/LoginRequest.java
package com.hexavolt.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank @Email String email,
    @NotBlank String password
) {}
