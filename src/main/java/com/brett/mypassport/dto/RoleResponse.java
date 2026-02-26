package com.brett.mypassport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Response object for role details")
public class RoleResponse {

    @Schema(description = "The unique identifier of the role", example = "1")
    private Long id;

    @Schema(description = "The unique name of the role", example = "ADMIN")
    private String name;

    @Schema(description = "A description of what this role entails", example = "Has full system access")
    private String description;

    @Schema(description = "Timestamp when the role was created")
    private LocalDateTime createdAt;

    @Schema(description = "The system this role belongs to", example = "sys-b")
    private String sysCode;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }
}
