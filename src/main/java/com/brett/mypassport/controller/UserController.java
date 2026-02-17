package com.brett.mypassport.controller;

import com.brett.mypassport.common.ApiConstants;
import com.brett.mypassport.dto.UserResponse;
import com.brett.mypassport.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;
import com.brett.mypassport.dto.DeviceResponse;
import org.springframework.core.annotation.Order;

@RestController
@RequestMapping(ApiConstants.API_V1 + "/users")
@Tag(name = "User", description = "APIs for user management")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get User Profile", description = "Retrieves the profile of the currently authenticated user.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Order(10)
    @GetMapping("/profile")
    public UserResponse getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new org.springframework.security.access.AccessDeniedException("User not authenticated");
        }
        return userService.getUserProfile(userDetails.getUsername());
    }

    @Operation(summary = "Change Password", description = "Allows authenticated users to change their password.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid old password or mismatching new passwords"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Order(11)
    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody com.brett.mypassport.dto.ChangePasswordRequest request) {
        if (userDetails == null) {
            throw new org.springframework.security.access.AccessDeniedException("User not authenticated");
        }
        userService.changePassword(userDetails.getUsername(), request);
        return "Password changed successfully";
    }

    @Operation(summary = "Get Active Devices", description = "Retrieves a list of active devices (sessions) for the user.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of devices retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Order(12)
    @GetMapping("/devices")
    public List<DeviceResponse> getActiveDevices(@AuthenticationPrincipal UserDetails userDetails, @RequestHeader("Authorization") String token) {
        if (userDetails == null) {
            throw new org.springframework.security.access.AccessDeniedException("User not authenticated");
        }
        return userService.getActiveDevices(userDetails.getUsername(), token);
    }
}
