// src/main/java/com/hexavolt/backend/service/impl/AuthServiceImpl.java
package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.dto.LoginRequest;
import com.hexavolt.backend.dto.RegisterRequest;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.mapper.UserMapper;
import com.hexavolt.backend.repository.CityRepository;
import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.service.AuthService;
import com.hexavolt.backend.service.JwtService;
import com.hexavolt.backend.service.MailService;
import com.hexavolt.backend.service.UserTokenService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final CityRepository cityRepo;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final UserTokenService tokenService;
    private final MailService mailService;

    private final String baseUrl = "http://localhost:8080"; // ⚠️ à externaliser plus tard

    public AuthServiceImpl(UserRepository userRepo,
                           CityRepository cityRepo,
                           PasswordEncoder encoder,
                           UserMapper userMapper,
                           JwtService jwtService,
                           UserTokenService tokenService,
                           MailService mailService) {
        this.userRepo = userRepo;
        this.cityRepo = cityRepo;
        this.encoder = encoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.mailService = mailService;
    }

    @Override
    public void register(RegisterRequest req) {
        var emailNorm = req.email().trim().toLowerCase();
        if (userRepo.existsByEmail(emailNorm)) {
            throw new IllegalArgumentException("Email already registered");
        }

        var city = cityRepo.findById(req.cityId())
                .orElseThrow(() -> new IllegalArgumentException("City not found"));

        var encodedPwd = encoder.encode(req.password());
        var user = userMapper.toEntity(req, city, encodedPwd);
        userRepo.save(user);

        // Génération du token d’activation
        var tk = tokenService.createActivationToken(user, Duration.ofHours(24));

        // Construction du lien d’activation
        var link = baseUrl + "/api/auth/verify?token=" + tk.getToken();

        // Envoi du mail
        mailService.send(
                user.getEmail(),
                "Vérifiez votre e-mail",
                "Bonjour " + user.getFirstName() + ",\n\nCliquez sur ce lien pour activer votre compte :\n" + link
        );
    }

    @Override
    public void verifyEmail(String token) {
        var t = tokenService.consumeActivationToken(token);
        var u = t.getUser();
        u.setEmailIsValid(true);
        userRepo.save(u);
    }

    @Override
    public String login(LoginRequest req) {
        var email = req.email().trim().toLowerCase();
        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!Boolean.TRUE.equals(user.getEmailIsValid())) {
            throw new BadCredentialsException("Email not verified");
        }
        if (!encoder.matches(req.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtService.generateToken(user);
    }

    @Override
    public void requestPasswordReset(String email) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'requestPasswordReset'");
    }

    @Override
    public void resetPassword(String token, String newPassword) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'resetPassword'");
    }
}
