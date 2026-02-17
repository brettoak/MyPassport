package com.brett.mypassport.controller;

import com.brett.mypassport.dto.ChangePasswordRequest;
import com.brett.mypassport.dto.UserResponse;
import com.brett.mypassport.service.UserService;
import com.brett.mypassport.common.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.context.annotation.Import;
import com.brett.mypassport.config.SecurityConfig;

import java.time.LocalDateTime;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser")
    public void testGetUserProfileSuccess() throws Exception {
        UserResponse mockResponse = new UserResponse(
                1L, "testuser", "test@example.com", LocalDateTime.now(), LocalDateTime.now());

        when(userService.getUserProfile("testuser")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    public void testGetUserProfileUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().isForbidden()); // SecurityConfig denies access
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testChangePasswordSuccess() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");
        request.setConfirmPassword("newPass");

        doNothing().when(userService).changePassword(eq("testuser"), any(ChangePasswordRequest.class));

        mockMvc.perform(post("/api/v1/users/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf())) // CSRF might be disabled but safe to add if needed, though config disables it
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("Password changed successfully"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testChangePasswordMismatch() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");
        request.setConfirmPassword("wrongPass");

        doThrow(new IllegalArgumentException("New passwords do not match"))
                .when(userService).changePassword(eq("testuser"), any(ChangePasswordRequest.class));

        mockMvc.perform(post("/api/v1/users/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("New passwords do not match"));
    }
}
