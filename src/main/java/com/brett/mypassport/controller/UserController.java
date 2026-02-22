package com.brett.mypassport.controller;

import com.brett.mypassport.common.ApiConstants;
import com.brett.mypassport.dto.UserResponse;
import com.brett.mypassport.dto.UserRoleRequest;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import com.brett.mypassport.dto.DeviceResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@RestController
@RequestMapping(ApiConstants.API_V1 + "/users")
@Tag(name = "User", description = "APIs for user management")
@Validated
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
    public List<DeviceResponse> getActiveDevices(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        if (userDetails == null) {
            throw new org.springframework.security.access.AccessDeniedException("User not authenticated");
        }
        String token = request.getHeader("Authorization");
        return userService.getActiveDevices(userDetails.getUsername(), token);
    }

    @Operation(summary = "Kick Device", description = "Revokes the session for a specific device/token.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device kicked successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access or token not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Order(13)
    @DeleteMapping("/devices/{tokenId}")
    public String kickDevice(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long tokenId, HttpServletRequest request) {
        if (userDetails == null) {
            throw new org.springframework.security.access.AccessDeniedException("User not authenticated");
        }
        String token = request.getHeader("Authorization");
        userService.revokeDevice(tokenId, userDetails.getUsername(), token);
        return "Device kicked successfully";
    }

    @Operation(summary = "Assign Roles to User", description = "Replaces the current roles of a user with a new set of roles.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles assigned successfully"),
            @ApiResponse(responseCode = "400", description = "User not found or invalid roles")
    })
    @Order(14)
    @PreAuthorize("hasAuthority(T(com.brett.mypassport.common.PermissionConstants).ROLE_ASSIGN)")
    @PostMapping("/{id}/roles")
    public String assignRoles(@PathVariable Long id, @RequestBody UserRoleRequest request) {
        userService.assignRolesToUser(id, request.getRoleIds());
        return "Roles assigned successfully";
    }

    @Operation(summary = "Get All Users", description = "Retrieves a paginated list of all users. Requires USER_VIEW permission.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User list retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @Order(15)
    @PreAuthorize("hasAuthority(T(com.brett.mypassport.common.PermissionConstants).USER_VIEW)")
    @GetMapping
    public Page<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index must not be less than zero") int page,
            @RequestParam(defaultValue = "10") @Min(value = 0, message = "Page size must not be less than zero") @Max(value = 30, message = "Page size must not be greater than 30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.getAllUsers(pageable);
    }

    @Operation(summary = "Get User by ID", description = "Retrieves a specific user's details. Requires USER_VIEW permission.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access")
    })
    @Order(16)
    @PreAuthorize("hasAuthority(T(com.brett.mypassport.common.PermissionConstants).USER_VIEW)")
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
