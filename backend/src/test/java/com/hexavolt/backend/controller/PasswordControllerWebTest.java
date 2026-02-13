package com.hexavolt.backend.controller;

import com.hexavolt.backend.dto.ResetPasswordConfirmDTO;
import com.hexavolt.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PasswordController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(PasswordControllerWebTest.TestSecurityConfig.class)
class PasswordControllerWebTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AuthService authService;

        @Test
        void requestReset_shouldReturn200_andDelegateToService() throws Exception {
                mockMvc.perform(post("/api/auth/password/reset/request")
                                .param("email", "User@Example.com"))
                                .andExpect(status().isOk());

                verify(authService).requestPasswordReset("User@Example.com");
        }

        @Test
        void confirmReset_shouldReturn200_andDelegateToService() throws Exception {
                mockMvc.perform(post("/api/auth/password/reset/confirm")
                                .contentType("application/json")
                                .content("""
                                                {
                                                  "token": "abc123",
                                                  "newPassword": "MonSuperMot2Passe!2025*"
                                                }
                                                """))
                                .andExpect(status().isOk());

                ArgumentCaptor<ResetPasswordConfirmDTO> captor = ArgumentCaptor.forClass(ResetPasswordConfirmDTO.class);

                verify(authService).confirmPasswordReset(captor.capture());

                ResetPasswordConfirmDTO sent = captor.getValue();
                assertEquals("abc123", sent.getToken());
                assertEquals("MonSuperMot2Passe!2025*", sent.getNewPassword());
        }

        @TestConfiguration
        static class TestSecurityConfig {

                @Bean
                SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                        return http
                                        .csrf(csrf -> csrf.disable())
                                        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                                        .build();
                }
        }
}
