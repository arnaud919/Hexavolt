package com.hexavolt.backend.dto;

import java.time.LocalDate;

public record UserResponse(
  Integer id,
  String firstName,
  String lastName,
  String address,
  String postalCode,
  String phone,
  LocalDate birthdate,
  Integer cityId,
  String email,
  Boolean emailIsValid
) {}