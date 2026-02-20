package com.brett.mypassport.controller;

import com.brett.mypassport.dto.ChangePasswordRequest;
import com.brett.mypassport.dto.DeviceResponse;
import com.brett.mypassport.dto.UserResponse;
import com.brett.mypassport.dto.UserRoleRequest;
import com.brett.mypassport.service.UserService;
import com.brett.mypassport.common.JwtUtil;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.context.annotation.Import;
import com.brett.mypassport.config.SecurityConfig;
import com.brett.mypassport.config.RsaKeyProperties;

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

    @MockBean
    private RsaKeyProperties rsaKeyProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser")
    public void testGetUserProfileSuccess() throws Exception {
        UserResponse mockResponse = new UserResponse(
                1L, "testuser", "test@example.com", LocalDateTime.now(), LocalDateTime.now(),
                java.util.Collections.emptySet(), java.util.Collections.emptySet()
        );

        when(userService.getUserProfile("testuser")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    public void testGetUserProfileUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile"))
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
    public void testGetActiveDevices() throws Exception {
        DeviceResponse device1 = new DeviceResponse(1L, "127.0.0.1", "Chrome", LocalDateTime.now(), true);
        DeviceResponse device2 = new DeviceResponse(2L, "192.168.1.5", "Firefox", LocalDateTime.now(), false);
        List<DeviceResponse> devices = Arrays.asList(device1, device2);

        when(userService.getActiveDevices(eq("testuser"), anyString())).thenReturn(devices);

        mockMvc.perform(get("/api/v1/users/devices")
                .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].ipAddress").value("127.0.0.1"))
                .andExpect(jsonPath("$.data[0].current").value(true))
                .andExpect(jsonPath("$.data[1].deviceInfo").value("Firefox"));
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

    @Test
    @WithMockUser(username = "testuser")
    public void testKickDeviceSuccess() throws Exception {
        Long tokenId = 123L;
        String token = "Bearer valid-token";

        doNothing().when(userService).revokeDevice(eq(tokenId), eq("testuser"), eq(token));

        mockMvc.perform(delete("/api/v1/users/devices/{tokenId}", tokenId)
                .header("Authorization", token)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("Device kicked successfully"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_MANAGE"})
    public void testAssignRolesSuccess() throws Exception {
        UserRoleRequest request = new UserRoleRequest();
        request.setRoleIds(Arrays.asList(1L, 2L));

        doNothing().when(userService).assignRolesToUser(eq(1L), eq(request.getRoleIds()));

        mockMvc.perform(post("/api/v1/users/1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("Roles assigned successfully"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_VIEW"})
    public void testGetAllUsersSuccess() throws Exception {
        UserResponse mockResponse = new UserResponse(
                1L, "testuser", "test@example.com", LocalDateTime.now(), LocalDateTime.now(),
                java.util.Collections.emptySet(), java.util.Collections.emptySet()
        );
        PageImpl<UserResponse> page = new PageImpl<>(Arrays.asList(mockResponse), PageRequest.of(0, 10), 1);

        when(userService.getAllUsers(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/users")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"USER_VIEW"})
    public void testGetUserByIdSuccess() throws Exception {
        UserResponse mockResponse = new UserResponse(
                1L, "testuser", "test@example.com", LocalDateTime.now(), LocalDateTime.now(),
                java.util.Collections.emptySet(), java.util.Collections.emptySet()
        );

        when(userService.getUserById(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/users/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }
}
