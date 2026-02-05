package com.hexavolt.backend.mapper;

import org.springframework.stereotype.Component;

import com.hexavolt.backend.dto.ProfileResponseDTO;
import com.hexavolt.backend.entity.User;

@Component
public class ProfileMapper {

    public ProfileResponseDTO toResponse(User user) {
        if (user == null) {
            return null;
        }

        ProfileResponseDTO dto = new ProfileResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setPostalCode(user.getPostalCode());
        dto.setBirthdate(user.getBirthdate());

        if (user.getCity() != null) {
            dto.setCityId(user.getCity().getId());
            dto.setCityName(user.getCity().getName());
        }

        return dto;
    }
}
