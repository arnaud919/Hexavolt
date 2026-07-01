package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.dto.LoginRequestDTO;
import com.hexavolt.backend.dto.RegisterRequestDTO;
import com.hexavolt.backend.entity.City;
import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.entity.UserToken;
import com.hexavolt.backend.exception.BusinessException;
import com.hexavolt.backend.mapper.UserMapper;
import com.hexavolt.backend.repository.CityRepository;
import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.service.JwtService;
import com.hexavolt.backend.service.MailService;
import com.hexavolt.backend.service.MailTemplateService;
import com.hexavolt.backend.service.UserTokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private CityRepository cityRepo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserTokenService tokenService;

    @Mock
    private MailService mailService;

    @Mock
    private MailTemplateService mailTemplates;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepo,
                cityRepo,
                encoder,
                userMapper,
                jwtService,
                tokenService,
                mailService,
                mailTemplates,
                "http://localhost:8080",
                "http://localhost:4200");
    }

    @Test
    void registerShouldCreateUserWhenEmailIsUnique() {
        RegisterRequestDTO request = createValidRegisterRequest("Test@Email.com");

        City city = new City();
        User user = new User();

        UserToken activationToken = new UserToken();
        activationToken.setToken("activation-token");

        when(userRepo.existsByEmail("test@email.com")).thenReturn(false);
        when(cityRepo.findById(request.cityId())).thenReturn(Optional.of(city));
        when(encoder.encode(request.password())).thenReturn("encoded-password");
        when(userMapper.toEntity(request, city, "encoded-password")).thenReturn(user);
        when(tokenService.createActivationToken(user, Duration.ofHours(24))).thenReturn(activationToken);
        when(mailTemplates.activationEmail(
                eq(user),
                eq("http://localhost:4200/verify?token=activation-token"))).thenReturn("<html>Activation</html>");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepo).existsByEmail("test@email.com");
        verify(userRepo).save(userCaptor.capture());
        verify(tokenService).createActivationToken(user, Duration.ofHours(24));
        verify(mailService).sendHtml(
                "test@email.com",
                "Activation de votre compte Hexavolt",
                "<html>Activation</html>");

        assertEquals("test@email.com", userCaptor.getValue().getEmail());
    }

    @Test
    void registerShouldRejectWhenEmailAlreadyExists() {
        RegisterRequestDTO request = createValidRegisterRequest("test@email.com");

        when(userRepo.existsByEmail("test@email.com")).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.register(request));

        assertEquals("Cette adresse e-mail est déjà enregistrée.", exception.getMessage());

        verify(userRepo).existsByEmail("test@email.com");
        verify(cityRepo, never()).findById(any());
        verify(userRepo, never()).save(any());
        verify(tokenService, never()).createActivationToken(any(), any());
        verify(mailService, never()).sendHtml(any(), any(), any());
    }

    private RegisterRequestDTO createValidRegisterRequest(String email) {
        return new RegisterRequestDTO(
                "Jean",
                "Dupont",
                "12 rue des Lilas",
                "75000",
                "0600000000",
                LocalDate.of(1996, 05, 07),
                54L,
                email,
                "MotDePasseSolide2026!");
    }

    @Test
    void verifyEmailShouldActivateUserWhenTokenIsValid() {
        String rawToken = "valid-activation-token";

        User user = new User();
        user.setEmailIsValid(false);

        UserToken token = new UserToken();
        token.setUser(user);

        when(tokenService.validateActivationToken(rawToken)).thenReturn(token);

        authService.verifyEmail(rawToken);

        assertTrue(Boolean.TRUE.equals(user.getEmailIsValid()));

        verify(tokenService).validateActivationToken(rawToken);
        verify(userRepo).save(user);
        verify(tokenService).consume(token);
    }

    @Test
    void loginShouldReturnJwtWhenUserIsActivatedAndCredentialsAreValid() {
        LoginRequestDTO request = new LoginRequestDTO(
                "Test@Email.com",
                "MotDePasseSolide2026!");

        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("encoded-password");
        user.setEmailIsValid(true);

        when(userRepo.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        when(encoder.matches("MotDePasseSolide2026!", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        String result = authService.login(request);

        assertEquals("jwt-token", result);

        verify(userRepo).findByEmail("test@email.com");
        verify(encoder).matches("MotDePasseSolide2026!", "encoded-password");
        verify(jwtService).generateToken(user);
    }
}