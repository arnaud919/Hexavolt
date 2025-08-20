package com.hexavolt.backend.mapper;

import com.hexavolt.backend.dto.RegisterRequest;
import com.hexavolt.backend.dto.UserResponse;
import com.hexavolt.backend.entity.City;
import com.hexavolt.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  /** Construit l'entité User à partir du RegisterRequest + City + mot de passe déjà encodé. */
  public User toEntity(RegisterRequest dto, City city, String encodedPassword) {
    User u = new User();
    u.setFirstName(trim(dto.firstName()));
    u.setLastName(trim(dto.lastName()));
    u.setAddress(trim(dto.address()));
    u.setPostalCode(trim(dto.postalCode()));
    u.setPhone(trimToNull(dto.phone()));
    u.setBirthdate(dto.birthdate());
    u.setCity(city);
    u.setEmail(trimLower(dto.email()));
    u.setPassword(encodedPassword);       // hashé en amont
    u.setEmailIsValid(false);             // inscription -> non validé
    return u;
  }

  /** Expose un DTO de sortie sans champs sensibles. */
  public UserResponse toResponse(User user) {
    return new UserResponse(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getAddress(),
        user.getPostalCode(),
        user.getPhone(),
        user.getBirthdate(),
        user.getCity() != null ? user.getCity().getId() : null,
        user.getEmail(),
        user.getEmailIsValid()
    );
  }

  // ------- petites aides “propres” -------
  private static String trim(String s) { return s == null ? null : s.trim(); }
  private static String trimLower(String s) { return s == null ? null : s.trim().toLowerCase(); }
  private static String trimToNull(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }
}

