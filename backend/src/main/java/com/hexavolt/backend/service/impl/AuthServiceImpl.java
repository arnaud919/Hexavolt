package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.dto.LoginRequestDTO;
import com.hexavolt.backend.dto.ProfileDTO;
import com.hexavolt.backend.dto.RegisterRequestDTO;
import com.hexavolt.backend.dto.ResetPasswordConfirmDTO;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.entity.UserToken;
import com.hexavolt.backend.exception.BusinessException;
import com.hexavolt.backend.exception.ResourceNotFoundException;
import com.hexavolt.backend.mapper.UserMapper;
import com.hexavolt.backend.repository.CityRepository;
import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.security.PasswordPolicy;
import com.hexavolt.backend.service.AuthService;
import com.hexavolt.backend.service.JwtService;
import com.hexavolt.backend.service.MailService;
import com.hexavolt.backend.service.MailTemplateService;
import com.hexavolt.backend.service.UserTokenService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

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
    private final MailTemplateService mailTemplates;

    private final String baseUrl;
    private final String frontendUrl;

    public AuthServiceImpl(
            UserRepository userRepo,
            CityRepository cityRepo,
            PasswordEncoder encoder,
            UserMapper userMapper,
            JwtService jwtService,
            UserTokenService tokenService,
            MailService mailService,
            MailTemplateService mailTemplates,
            @Value("${app.base-url}") String baseUrl,
            @Value("${app.frontend-url}") String frontendUrl
    ) {
        this.userRepo = userRepo;
        this.cityRepo = cityRepo;
        this.encoder = encoder;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.mailService = mailService;
        this.mailTemplates = mailTemplates;
        this.baseUrl = baseUrl;
        this.frontendUrl = frontendUrl;
    }

    @Override
    @Transactional
    public void register(RegisterRequestDTO req) {
        String emailNorm = normalizeEmail(req.email());

        PasswordPolicy.assertNoPersonalInfo(
                req.password(),
                emailNorm,
                buildFullName(req.firstName(), req.lastName())
        );

        if (userRepo.existsByEmail(emailNorm)) {
            throw new BusinessException("Cette adresse e-mail est déjà enregistrée.");
        }

        var city = cityRepo.findById(req.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("Ville introuvable."));

        var encodedPwd = encoder.encode(req.password());
        var user = userMapper.toEntity(req, city, encodedPwd);
        user.setEmail(emailNorm);

        userRepo.save(user);

        var token = tokenService.createActivationToken(user, Duration.ofHours(24));
        var link = frontendUrl + "/verify?token=" + token.getToken();

        String html = mailTemplates.activationEmail(user, link);
        mailService.sendHtml(user.getEmail(), "Activation de votre compte Hexavolt", html);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        UserToken userToken = tokenService.validateActivationToken(token);

        User user = userToken.getUser();
        user.setEmailIsValid(true);

        userRepo.save(user);
        tokenService.consume(userToken);
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {
        String emailNorm = normalizeEmail(email);

        userRepo.findByEmail(emailNorm).ifPresent(user -> {
            if (Boolean.TRUE.equals(user.getEmailIsValid())) {
                return;
            }

            if (!tokenService.canResendActivation(user, Duration.ofMinutes(2))) {
                return;
            }

            var token = tokenService.createActivationToken(user, Duration.ofHours(24));
            var link = frontendUrl + "/verify?token=" + token.getToken();

            String html = mailTemplates.activationEmail(user, link);
            mailService.sendHtml(user.getEmail(), "Activation de votre compte Hexavolt", html);
        });
    }

    @Override
    public String login(LoginRequestDTO req) {
        String email = normalizeEmail(req.email());

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Identifiants invalides."));

        if (!Boolean.TRUE.equals(user.getEmailIsValid())) {
            throw new BadCredentialsException("Identifiants invalides.");
        }

        if (!encoder.matches(req.password(), user.getPassword())) {
            throw new BadCredentialsException("Identifiants invalides.");
        }

        return jwtService.generateToken(user);
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        String emailNorm = normalizeEmail(email);

        Optional<User> userOpt = userRepo.findByEmail(emailNorm);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            tokenService.invalidateAllResetTokens(user.getId());

            UserToken token = tokenService.createResetPasswordToken(user, Duration.ofHours(2));
            String link = baseUrl + "/reset-password?token=" + token.getToken();

            String html = mailTemplates.resetPasswordEmail(user, link);
            mailService.sendHtml(user.getEmail(), "Réinitialisation de votre mot de passe", html);
        }
    }

    @Override
    @Transactional
    public void confirmPasswordReset(ResetPasswordConfirmDTO dto) {
        UserToken token = tokenService.validateResetPasswordToken(dto.getToken());
        User user = token.getUser();

        String fullName = buildFullName(user.getFirstName(), user.getLastName());

        PasswordPolicy.assertNoPersonalInfo(
                dto.getNewPassword(),
                user.getEmail(),
                fullName
        );

        user.setPassword(encoder.encode(dto.getNewPassword()));
        userRepo.save(user);

        tokenService.consume(token);
    }

    @Override
    public ProfileDTO getProfile() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        User user = (User) authentication.getPrincipal();

        return new ProfileDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessException("Une adresse e-mail est requise.");
        }

        return email.trim().toLowerCase();
    }

    private String buildFullName(String firstName, String lastName) {
        return ((firstName == null ? "" : firstName) + " " +
                (lastName == null ? "" : lastName)).trim();
    }
}