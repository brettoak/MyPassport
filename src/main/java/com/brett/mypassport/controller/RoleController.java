package com.brett.mypassport.controller;

import com.brett.mypassport.common.ApiConstants;
import com.brett.mypassport.dto.RoleRequest;
import com.brett.mypassport.dto.RoleResponse;
import com.brett.mypassport.dto.RolePermissionRequest;
import com.brett.mypassport.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.annotation.Order;

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping(ApiConstants.API_V1 + "/roles")
@Tag(name = "Role", description = "APIs for Role management (RBAC)")
// For now, requiring bearerAuth for all Role APIs. Later, you can restrict this to admins only.
@SecurityRequirement(name = "bearerAuth") 
@Validated
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "Create Role", description = "Creates a new system role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or role already exists")
    })
    @Order(20)
    @PreAuthorize("hasAuthority(T(com.brett.mypassport.common.PermissionConstants).ROLE_CREATE)")
    @PostMapping
    public RoleResponse createRole(@RequestBody RoleRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        return roleService.createRole(request);
    }

    @Operation(summary = "Update Role", description = "Updates an existing role's name or description.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or role not found")
    })
    @Order(21)
    @PreAuthorize("hasAuthority(T(com.brett.mypassport.common.PermissionConstants).ROLE_UPDATE)")
    @PutMapping("/{id}")
    public RoleResponse updateRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        return roleService.updateRole(id, request);
    }

    @Operation(summary = "Delete Role", description = "Deletes a role by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Role not found")
    })
    @Order(22)
    @PreAuthorize("hasAuthority(T(com.brett.mypassport.common.PermissionConstants).ROLE_DELETE)")
    @DeleteMapping("/{id}")
    public com.brett.mypassport.common.ApiResponse<String> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return com.brett.mypassport.common.ApiResponse.success("Role deleted successfully");
    }

    @Operation(summary = "Get All Roles", description = "Retrieves a paginated list of all roles in the system.")
    @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    @Order(23)
    @PreAuthorize("hasAuthority(T(com.brett.mypassport.common.PermissionConstants).ROLE_VIEW)")
    @GetMapping
    public Page<RoleResponse> getAllRoles(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index must not be less than zero") int page,
            @RequestParam(defaultValue = "10") @Min(value = 0, message = "Page size must not be less than zero") @Max(value = 30, message = "Page size must not be greater than 30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return roleService.getAllRoles(pageable);
    }

    @Operation(summary = "Get Role by ID", description = "Retrieves details of a specific role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Role not found")
    })
    @Order(24)
    @PreAuthorize("hasAuthority(T(com.brett.mypassport.common.PermissionConstants).ROLE_VIEW)")
    @GetMapping("/{id}")
    public RoleResponse getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @Operation(summary = "Assign Permissions to Role", description = "Replaces the current permissions of a role with a new set of permissions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissions assigned successfully"),
            @ApiResponse(responseCode = "400", description = "Role not found or invalid permissions")
    })
    @Order(25)
    @PreAuthorize("hasAuthority(T(com.brett.mypassport.common.PermissionConstants).ROLE_UPDATE)")
    @PostMapping("/{id}/permissions")
    public com.brett.mypassport.common.ApiResponse<String> assignPermissions(@PathVariable Long id, @RequestBody RolePermissionRequest request) {
        roleService.assignPermissionsToRole(id, request.getPermissionIds());
        return com.brett.mypassport.common.ApiResponse.success("Permissions assigned successfully");
    }
}
