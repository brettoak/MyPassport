package com.brett.mypassport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request object for assigning roles to a user")
public class UserRoleRequest {

    @Schema(description = "A list of role IDs to assign to the user", example = "[1]")
    @NotNull(message = "Role IDs cannot be null")
    @NotEmpty(message = "Role IDs cannot be empty")
    private List<Long> roleIds;

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
