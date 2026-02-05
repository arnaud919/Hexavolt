package com.hexavolt.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexavolt.backend.dto.ProfileResponseDTO;
import com.hexavolt.backend.dto.ProfileUpdateRequestDTO;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.service.ProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ProfileResponseDTO> getProfile() {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        ProfileResponseDTO response =
                profileService.getProfile(user);

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ProfileResponseDTO> updateProfile(
            @Valid @RequestBody ProfileUpdateRequestDTO request) {

        ProfileResponseDTO response = profileService.updateProfile(request);
        return ResponseEntity.ok(response);
    }
}
