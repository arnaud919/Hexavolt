// src/main/java/com/hexavolt/backend/service/impl/AuthServiceImpl.java
package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.dto.LoginRequestDTO;
import com.hexavolt.backend.dto.ProfileDTO;
import com.hexavolt.backend.dto.RegisterRequestDTO;
import com.hexavolt.backend.dto.ResetPasswordConfirmDTO;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.entity.UserToken;
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

    String baseUrl;
    String frontendUrl;

    public AuthServiceImpl(UserRepository userRepo,
            CityRepository cityRepo,
            PasswordEncoder encoder,
            UserMapper userMapper,
            JwtService jwtService,
            UserTokenService tokenService,
            MailService mailService,
            MailTemplateService mailTemplates,
            @Value("${app.base-url}") String baseUrl,
            @Value("${app.frontend-url}") String frontendUrl) {

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

        PasswordPolicy.assertNoPersonalInfo(
                req.password(),
                req.email(),
                (req.firstName() == null ? "" : req.firstName()) + " "
                        + (req.lastName() == null ? "" : req.lastName()));

        var emailNorm = req.email().trim().toLowerCase();

        if (userRepo.existsByEmail(emailNorm)) {
            throw new IllegalArgumentException("Cette adresse e-mail est déjà enregistrée.");
        }

        var city = cityRepo.findById(req.cityId())
                .orElseThrow(() -> new IllegalArgumentException("Ville introuvable."));

        var encodedPwd = encoder.encode(req.password());
        var user = userMapper.toEntity(req, city, encodedPwd);
        user.setEmail(emailNorm);
        userRepo.save(user);

        var tk = tokenService.createActivationToken(user, Duration.ofHours(24));

        var link = frontendUrl + "/verify?token=" + tk.getToken();

        String html = mailTemplates.activationEmail(user, link);
        mailService.sendHtml(user.getEmail(), "Activation de votre compte Hexavolt", html);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {

        UserToken tk = tokenService.validateActivationToken(token);

        User user = tk.getUser();
        user.setEmailIsValid(true);
        userRepo.save(user);

        tokenService.consume(tk);
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {

        var emailNorm = email.trim().toLowerCase();

        userRepo.findByEmail(emailNorm).ifPresent(user -> {

            if (Boolean.TRUE.equals(user.getEmailIsValid())) {
                return;
            }

            if (!tokenService.canResendActivation(user, Duration.ofMinutes(2))) {
                return;
            }

            var tk = tokenService.createActivationToken(user, Duration.ofHours(24));
            var link = frontendUrl + "/verify?token=" + tk.getToken();

            String html = mailTemplates.activationEmail(user, link);
            mailService.sendHtml(user.getEmail(), "Activation de votre compte Hexavolt", html);
        });

    }

    @Override
    public String login(LoginRequestDTO req) {
        var email = req.email().trim().toLowerCase();
        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!Boolean.TRUE.equals(user.getEmailIsValid())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        if (!encoder.matches(req.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtService.generateToken(user);
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        // Normaliser l'email (anti NPE et éviter la casse)
        String emailNorm = (email == null) ? "" : email.trim().toLowerCase();

        Optional<User> userOpt = userRepo.findByEmail(emailNorm);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Invalider d’anciens tokens de reset
            tokenService.invalidateAllResetTokens(user.getId());

            // Créer un nouveau token (2h de validité)
            UserToken tk = tokenService.createResetPasswordToken(user, Duration.ofHours(2));

            // Lien front (ou API) pour réinitialiser
            String link = baseUrl + "/reset-password?token=" + tk.getToken();

            // Envoi du mail
            String html = mailTemplates.resetPasswordEmail(user, link);
            mailService.sendHtml(user.getEmail(), "Réinitialisation de votre mot de passe", html);
        }
        // Côté contrôleur : toujours 200 pour ne pas révéler si l'email existe.
    }

    @Override
    @Transactional
    public void confirmPasswordReset(ResetPasswordConfirmDTO dto) {
        // 1) Valider le jeton RESET_PASSWORD
        UserToken tk = tokenService.validateResetPasswordToken(dto.getToken());
        User user = tk.getUser();

        // 2) Politique mot de passe
        String fullName = ((user.getFirstName() == null ? "" : user.getFirstName()) +
                " " +
                (user.getLastName() == null ? "" : user.getLastName())).trim();

        PasswordPolicy.assertNoPersonalInfo(dto.getNewPassword(), user.getEmail(), fullName);

        // 3) Mise à jour + encodage
        user.setPassword(encoder.encode(dto.getNewPassword()));
        userRepo.save(user);

        // 4) Consommer le jeton (usage unique)
        tokenService.consume(tk);
    }

    @Override
    public ProfileDTO getProfile() {

        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        return new ProfileDTO(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail()
        );
    }
}
