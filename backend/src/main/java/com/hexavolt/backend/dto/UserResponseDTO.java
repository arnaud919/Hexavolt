package com.hexavolt.backend.dto;

import java.time.LocalDate;

public record UserResponseDTO(
    Long id,
    String firstName,
    String lastName,
    String address,
    String postalCode,
    String phone,
    LocalDate birthdate,
    Long cityId,
    String email,
    Boolean emailIsValid) {
}