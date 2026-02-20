package com.brett.mypassport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Request object for assigning permissions to a role")
public class RolePermissionRequest {

    @Schema(description = "A list of permission IDs to assign to the role", example = "[1, 2, 3]")
    private List<Long> permissionIds;

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
