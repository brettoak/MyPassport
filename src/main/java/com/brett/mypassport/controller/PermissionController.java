package com.brett.mypassport.controller;

import com.brett.mypassport.common.ApiConstants;
import com.brett.mypassport.dto.PermissionResponse;
import com.brett.mypassport.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping(ApiConstants.API_V1 + "/permissions")
@Tag(name = "Permission", description = "APIs for Permission management (RBAC)")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "Get All Permissions", description = "Retrieves a list of all predefined system permissions.")
    @ApiResponse(responseCode = "200", description = "Permissions retrieved successfully")
    @Order(30)
    @PreAuthorize("hasAuthority(T(com.brett.mypassport.common.PermissionConstants).PERMISSION_VIEW)")
    @GetMapping
    public List<PermissionResponse> getAllPermissions() {
        return permissionService.getAllPermissions();
    }
}
