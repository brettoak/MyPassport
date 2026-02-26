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

import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping(value = ApiConstants.API_V1 + "/permissions", produces = "application/json")
@Tag(name = "Permission", description = "APIs for Permission management (RBAC)")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "Get All Permissions", description = "Retrieves a paginated list of all predefined system permissions.")
    @ApiResponse(responseCode = "200", description = "Permissions retrieved successfully")
    @Order(30)
    @PreAuthorize("hasAuthority(T(com.brett.mypassport.common.PermissionConstants).PERMISSION_VIEW)")
    @GetMapping
    public Page<PermissionResponse> getAllPermissions(
            @Parameter(description = "Optional system code to filter permissions by", example = "sys-b") @RequestParam(required = false) String sysCode,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index must not be less than zero") int page,
            @RequestParam(defaultValue = "10") @Min(value = 0, message = "Page size must not be less than zero") @Max(value = 30, message = "Page size must not be greater than 30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return permissionService.getAllPermissions(sysCode, pageable);
    }
}
