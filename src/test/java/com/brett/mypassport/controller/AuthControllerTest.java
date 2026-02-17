package com.brett.mypassport.controller;

import com.brett.mypassport.dto.LoginRequest;
import com.brett.mypassport.dto.LoginResponse;
import com.brett.mypassport.service.UserService;
import com.brett.mypassport.service.VerificationService;
import com.brett.mypassport.common.JwtUtil; // In case we need to mock it, though simplified here
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.brett.mypassport.dto.ResetPasswordRequest;
import com.brett.mypassport.dto.VerificationRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.context.annotation.Import;
import com.brett.mypassport.config.SecurityConfig;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @MockBean
        private VerificationService verificationService; // Required because AuthController injects it

        @MockBean
        private JwtUtil jwtUtil;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        public void testLoginSuccess() throws Exception {
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setEmail("test@example.com");
                loginRequest.setPassword("password123");

                LoginResponse loginResponse = new LoginResponse(
                                "testuser",
                                "test@example.com",
                                "fake-jwt-token",
                                3600000L,
                                "fake-refresh-token",
                                604800000L);

                when(userService.login(any(LoginRequest.class))).thenReturn(loginResponse);

                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.token").value("fake-jwt-token"))
                                .andExpect(jsonPath("$.data.username").value("testuser"));
        }

        @Test
        public void testLoginFailure() throws Exception {
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setEmail("test@example.com");
                loginRequest.setPassword("wrongpassword");

                when(userService.login(any(LoginRequest.class)))
                                .thenThrow(new IllegalArgumentException("Invalid email or password."));

                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(400))
                                .andExpect(jsonPath("$.message").value("Invalid email or password."));
        }


        @Test
        public void testForgotPasswordSuccess() throws Exception {
                VerificationRequest request = new VerificationRequest();
                request.setEmail("test@example.com");

                doNothing().when(userService).requestPasswordReset(anyString());

                mockMvc.perform(post("/api/v1/auth/forgot-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data").value("Verification code sent successfully."));
        }

        @Test
        public void testResetPasswordSuccess() throws Exception {
                ResetPasswordRequest request = new ResetPasswordRequest();
                request.setEmail("test@example.com");
                request.setVerificationCode("123456");
                request.setNewPassword("newpassword");
                request.setConfirmPassword("newpassword");

                doNothing().when(userService).resetPassword(any(ResetPasswordRequest.class));

                mockMvc.perform(post("/api/v1/auth/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data").value("Password reset successfully."));
        }

        @Test
        public void testResetPasswordMismatch() throws Exception {
                ResetPasswordRequest request = new ResetPasswordRequest();
                request.setEmail("test@example.com");
                request.setVerificationCode("123456");
                request.setNewPassword("newpassword");
                request.setConfirmPassword("mismatch");

                doThrow(new IllegalArgumentException("Passwords do not match.")).when(userService).resetPassword(any(ResetPasswordRequest.class));

                mockMvc.perform(post("/api/v1/auth/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(400))
                                .andExpect(jsonPath("$.message").value("Passwords do not match."));
        }
}
