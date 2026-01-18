package com.hexavolt.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ResetPasswordRequestDTO {
    @NotBlank
    @Email
    private String email;

    public ResetPasswordRequestDTO() {
    }

    public ResetPasswordRequestDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
