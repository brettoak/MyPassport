package com.brett.mypassport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request object for assigning permissions to a role")
public class RolePermissionRequest {

    @Schema(description = "A list of permission IDs to assign to the role", example = "[1, 2, 3]")
    @NotNull(message = "Permission IDs cannot be null")
    @NotEmpty(message = "Permission IDs cannot be empty")
    private List<Long> permissionIds;

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
