package com.brett.mypassport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for creating or updating a role")
public class RoleRequest {

    @Schema(description = "The unique name of the role (e.g., ADMIN, EDITOR)", example = "EDITOR")
    @NotBlank(message = "Role name cannot be empty")
    private String name;

    @Schema(description = "A description of what this role entails", example = "Can edit content but cannot delete users")
    private String description;

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
