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

import static org.mockito.ArgumentMatchers.any;
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
                                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                                .andExpect(jsonPath("$.username").value("testuser"));
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
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$").value("Invalid email or password."));
        }
}
