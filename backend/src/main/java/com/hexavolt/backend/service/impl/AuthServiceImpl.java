package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.dto.RegisterRequest;
import com.hexavolt.backend.dto.UserResponse;
import com.hexavolt.backend.entity.City;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.exception.ConflictException;
import com.hexavolt.backend.exception.ResourceNotFoundException;
import com.hexavolt.backend.repository.CityRepository;
import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepo;
  private final CityRepository cityRepo;
  private final PasswordEncoder passwordEncoder;

  public AuthServiceImpl(UserRepository userRepo, CityRepository cityRepo, PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.cityRepo = cityRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserResponse register(RegisterRequest req) {
    // normalisations classiques
    var emailNorm = req.email().trim().toLowerCase();
    if (userRepo.existsByEmail(emailNorm)) throw new ConflictException("Email already registered");

    City city = cityRepo.findById(req.cityId())
        .orElseThrow(() -> new ResourceNotFoundException("City not found"));

    // hashage (le champ entity s'appelle 'password' mais contient le hash)
    var user = new User();
    user.setFirstName(req.firstName().trim());
    user.setLastName(req.lastName().trim());
    user.setAddress(req.address().trim());
    user.setPostalCode(req.postalCode().trim());
    user.setPhone(req.phone() != null ? req.phone().trim() : null);
    user.setBirthdate(req.birthdate());
    user.setCity(city);
    user.setEmail(emailNorm);
    user.setPassword(passwordEncoder.encode(req.password())); // hash en base
    user.setEmailIsValid(false); // on activera plus tard via un lien d’activation

    userRepo.save(user);

    return new UserResponse(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getAddress(),
        user.getPostalCode(),
        user.getPhone(),
        user.getBirthdate(),
        user.getCity().getId(),
        user.getEmail(),
        user.getEmailIsValid()
    );
  }
}