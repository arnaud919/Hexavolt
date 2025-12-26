package com.hexavolt.backend.controller;

import com.hexavolt.backend.dto.ResetPasswordConfirm;
import com.hexavolt.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PasswordController.class)
@AutoConfigureMockMvc(addFilters = false)
class PasswordControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void requestReset_shouldReturn200_andDelegateToService() throws Exception {
        mockMvc.perform(post("/api/auth/password/reset/request")
                .param("email", "User@Example.com"))
                .andDo(print())
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
                .andDo(print())
                .andExpect(status().isOk());

        verify(authService).confirmPasswordReset(
                org.mockito.ArgumentMatchers.any(ResetPasswordConfirm.class));

        ArgumentCaptor<ResetPasswordConfirm> captor = ArgumentCaptor.forClass(ResetPasswordConfirm.class);
        verify(authService).confirmPasswordReset(captor.capture());

        ResetPasswordConfirm sent = captor.getValue();
        assertEquals("abc123", sent.getToken());
        assertEquals("MonSuperMot2Passe!2025*", sent.getNewPassword());
    }
}
