package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.entity.User;
import com.hexavolt.backend.entity.UserToken;
import com.hexavolt.backend.repository.UserRepository;
import com.hexavolt.backend.service.MailService;
import com.hexavolt.backend.service.MailTemplateService;
import com.hexavolt.backend.service.UserTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private UserRepository userRepo;
    private PasswordEncoder encoder;
    private UserTokenService tokenService;
    private MailService mailService;
    private MailTemplateService mailTemplates;
    
    private AuthServiceImpl service;

    @BeforeEach
    void setup() {
        userRepo = mock(UserRepository.class);
        encoder = mock(PasswordEncoder.class);
        tokenService = mock(UserTokenService.class);
        mailService = mock(MailService.class);
        mailTemplates = mock(MailTemplateService.class);

        service = new AuthServiceImpl(userRepo, null, encoder, null, null, tokenService, mailService, mailTemplates, null);
        // injecte si besoin la baseUrl :
        service.baseUrl = "https://app.hexavolt.com";
    }

    @Test
    void requestPasswordReset_shouldDoNothing_ifEmailNotFound() {
        when(userRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        service.requestPasswordReset("unknown@example.com");

        verifyNoInteractions(tokenService, mailService, mailTemplates);
    }

    @Test
    void requestPasswordReset_shouldCreateToken_andSendMail_ifUserExists() {
        User user = new User();
        user.setId(42);
        user.setEmail("user@example.com");
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserToken tk = new UserToken();
        tk.setToken("tok123");
        when(tokenService.createResetPasswordToken(eq(user), any(Duration.class))).thenReturn(tk);
        when(mailTemplates.resetPasswordEmail(eq(user), anyString())).thenReturn("<html>...</html>");

        service.requestPasswordReset("user@example.com");

        verify(tokenService).invalidateAllResetTokens(42);
        verify(tokenService).createResetPasswordToken(eq(user), any(Duration.class));

        ArgumentCaptor<String> linkCap = ArgumentCaptor.forClass(String.class);
        verify(mailTemplates).resetPasswordEmail(eq(user), linkCap.capture());
        assertEquals("https://app.hexavolt.com/reset-password?token=tok123", linkCap.getValue());

        verify(mailService).sendHtml(eq("user@example.com"), anyString(), anyString());
    }

    @Test
    void resetPassword_shouldValidateToken_CheckPolicy_Encode_Save_Consume() {
        // given
        User user = new User();
        user.setId(7);
        user.setEmail("user@example.com");
        user.setFirstName("Jean");
        user.setLastName("Dupont");

        UserToken tk = new UserToken();
        tk.setToken("tok123");
        tk.setUser(user);

        when(tokenService.validateResetPasswordToken("tok123")).thenReturn(tk);
        when(encoder.encode("Mot2Passe!Fort2025")).thenReturn("ENCODED");

        // when
        service.resetPassword("tok123", "Mot2Passe!Fort2025");

        // then
        verify(tokenService).validateResetPasswordToken("tok123");
        // PasswordPolicy est statique, on ne peut pas le vérifier directement ici ; on vérifie effets
        verify(encoder).encode("Mot2Passe!Fort2025");
        verify(userRepo).save(user);
        verify(tokenService).consume(tk);

        assertEquals("ENCODED", user.getPassword());
    }
}
