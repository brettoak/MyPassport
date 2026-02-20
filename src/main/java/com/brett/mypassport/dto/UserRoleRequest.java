package com.brett.mypassport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Request object for assigning roles to a user")
public class UserRoleRequest {

    @Schema(description = "A list of role IDs to assign to the user", example = "[1]")
    private List<Long> roleIds;

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
