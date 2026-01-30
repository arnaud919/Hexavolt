package com.hexavolt.backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

import com.hexavolt.backend.validation.StrongPassword;

public record RegisterRequestDTO(

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    String firstName,

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    String lastName,

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    String address,

    @NotBlank(message = "Le code postal est obligatoire")
    @Size(max = 10, message = "Le code postal ne doit pas dépasser 10 caractères")
    String postalCode,

    @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères")
    String phone,

    @NotNull(message = "La date de naissance est obligatoire")
    LocalDate birthdate,

    @NotNull(message = "La ville est obligatoire")
    Long cityId,

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    String email,

    @NotBlank(message = "Le mot de passe est obligatoire")
    @StrongPassword
    String password
) {}

