package com.hexavolt.backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record RegisterRequest(
  @NotBlank @Size(max = 50)  String firstName,
  @NotBlank @Size(max = 100) String lastName,
  @NotBlank @Size(max = 255) String address,
  @NotBlank @Size(max = 10)  String postalCode,
  @Size(max = 20)            String phone,    // optionnel
  @NotNull @Past             LocalDate birthdate,
  @NotNull                   Integer cityId,
  @NotBlank @Email @Size(max = 100) String email,
  @NotBlank @Size(min = 8, max = 100) String password
) {}
