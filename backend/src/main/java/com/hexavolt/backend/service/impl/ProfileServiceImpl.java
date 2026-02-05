package com.hexavolt.backend.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hexavolt.backend.dto.ProfileResponseDTO;
import com.hexavolt.backend.dto.ProfileUpdateRequestDTO;
import com.hexavolt.backend.entity.City;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.mapper.ProfileMapper;
import com.hexavolt.backend.mapper.UserMapper;
import com.hexavolt.backend.repository.CityRepository;
import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.service.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final ProfileMapper profileMapper;
    private final UserMapper userMapper;

    public ProfileServiceImpl(UserRepository userRepository,
                              CityRepository cityRepository,
                              ProfileMapper profileMapper,
                              UserMapper userMapper) {
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.profileMapper = profileMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDTO getProfile(User user) {
        return profileMapper.toResponse(user);
    }

    @Override
    public ProfileResponseDTO updateProfile(ProfileUpdateRequestDTO request) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not authenticated"
            );
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"
                ));

        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "City not found"
                ));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setPostalCode(request.getPostalCode());
        user.setBirthdate(request.getBirthdate());
        user.setCity(city);

        userRepository.save(user);

        return userMapper.toProfileResponse(user);
    }
}
