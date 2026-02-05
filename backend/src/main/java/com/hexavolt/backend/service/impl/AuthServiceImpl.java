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

        // 1) Règles de sécurité sur le mot de passe
        PasswordPolicy.assertNoPersonalInfo(
                req.password(),
                req.email(),
                (req.firstName() == null ? "" : req.firstName()) + " "
                        + (req.lastName() == null ? "" : req.lastName()));

        PasswordPolicy.assertStrongEnough(req.password());

        // 2) Normalisation de l'email
        var emailNorm = req.email().trim().toLowerCase();

        // 3) Unicité de l'email
        if (userRepo.existsByEmail(emailNorm)) {
            throw new IllegalArgumentException("Cette adresse e-mail est déjà enregistrée.");
        }

        // 4) Récupération de la ville
        var city = cityRepo.findById(req.cityId())
                .orElseThrow(() -> new IllegalArgumentException("Ville introuvable."));

        // 5) Encodage du mot de passe et création de l'utilisateur
        var encodedPwd = encoder.encode(req.password());
        var user = userMapper.toEntity(req, city, encodedPwd);
        user.setEmail(emailNorm); // s'assurer qu'on persiste l'email normalisé
        userRepo.save(user);

        // 6) Génération du token d’activation (valable 24h)
        var tk = tokenService.createActivationToken(user, Duration.ofHours(24));

        // 7) Construction du lien d’activation
        var link = frontendUrl + "/verify?token=" + tk.getToken();

        // 8) Envoi du mail d’activation
        String html = mailTemplates.activationEmail(user, link);
        mailService.sendHtml(user.getEmail(), "Activation de votre compte Hexavolt", html);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        // 1) Valider le jeton d’activation (non expiré, non utilisé, bon type)
        UserToken tk = tokenService.validateActivationToken(token);

        // 2) Marquer l'email comme validé
        User user = tk.getUser();
        user.setEmailIsValid(true);
        userRepo.save(user);

        // 3) Consommer le jeton (one-time)
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

            // Anti-spam : 1 renvoi toutes les 2 minutes (exemple)
            if (!tokenService.canResendActivation(user, Duration.ofMinutes(2))) {
                return;
            }

            var tk = tokenService.createActivationToken(user, Duration.ofHours(24));
            var link = frontendUrl + "/verify?token=" + tk.getToken();

            String html = mailTemplates.activationEmail(user, link);
            mailService.sendHtml(user.getEmail(), "Activation de votre compte Hexavolt", html);
        });

        // Réponse neutre côté controller, quoi qu’il arrive
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
        PasswordPolicy.assertStrongEnough(dto.getNewPassword());

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
