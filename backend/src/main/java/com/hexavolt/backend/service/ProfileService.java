package com.hexavolt.backend.service;

import com.hexavolt.backend.dto.ProfileResponseDTO;
import com.hexavolt.backend.dto.ProfileUpdateRequestDTO;
import com.hexavolt.backend.entity.User;

public interface ProfileService {

    ProfileResponseDTO getProfile(User user);

    ProfileResponseDTO updateProfile(ProfileUpdateRequestDTO request);
}
