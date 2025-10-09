package com.hexavolt.backend.controller;

import static org.mockito.Mockito.verify;

import com.hexavolt.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PasswordController.class)
class PasswordControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

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
                        .param("token", "abc123")
                        .param("password", "MonSuperMot2Passe!2025"))
                .andExpect(status().isOk());

        verify(authService).resetPassword("abc123", "MonSuperMot2Passe!2025");
    }
}
