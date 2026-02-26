package com.brett.mypassport.dto;

public class PermissionCheckResponse {

    private boolean valid;
    private boolean hasPermission;
    private String username;
    private String reason;

    public PermissionCheckResponse() {
    }

    public PermissionCheckResponse(boolean valid, boolean hasPermission, String username, String reason) {
        this.valid = valid;
        this.hasPermission = hasPermission;
        this.username = username;
        this.reason = reason;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isHasPermission() {
        return hasPermission;
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
