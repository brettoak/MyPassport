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

@RestController
@RequestMapping(ApiConstants.API_V1 + "/roles")
@Tag(name = "Role", description = "APIs for Role management (RBAC)")
// For now, requiring bearerAuth for all Role APIs. Later, you can restrict this to admins only.
@SecurityRequirement(name = "bearerAuth") 
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "Create Role", description = "Creates a new system role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or role already exists")
    })
    @Order(20)
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
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
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
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
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @DeleteMapping("/{id}")
    public String deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return "Role deleted successfully";
    }

    @Operation(summary = "Get All Roles", description = "Retrieves a list of all roles in the system.")
    @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    @Order(23)
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    @GetMapping
    public List<RoleResponse> getAllRoles() {
        return roleService.getAllRoles();
    }

    @Operation(summary = "Get Role by ID", description = "Retrieves details of a specific role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Role not found")
    })
    @Order(24)
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
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
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    @PostMapping("/{id}/permissions")
    public String assignPermissions(@PathVariable Long id, @RequestBody RolePermissionRequest request) {
        roleService.assignPermissionsToRole(id, request.getPermissionIds());
        return "Permissions assigned successfully";
    }
}
