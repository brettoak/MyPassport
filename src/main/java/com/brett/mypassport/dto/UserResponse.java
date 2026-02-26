package com.brett.mypassport.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Set<String>> roles;
    private Map<String, Set<String>> permissions;

    public UserResponse() {
    }

    public UserResponse(Long id, String username, String email, LocalDateTime createdAt, LocalDateTime updatedAt, Map<String, Set<String>> roles, Map<String, Set<String>> permissions) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.roles = roles;
        this.permissions = permissions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, Set<String>> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, Set<String>> roles) {
        this.roles = roles;
    }

    public Map<String, Set<String>> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, Set<String>> permissions) {
        this.permissions = permissions;
    }
}
