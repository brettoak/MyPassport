package com.brett.mypassport.dto;

import jakarta.validation.constraints.NotBlank;

public class PermissionCheckRequest {

    @NotBlank(message = "Token cannot be empty")
    private String token;

    @NotBlank(message = "System code cannot be empty")
    private String sysCode;

    private String requiredPermission;

    private String path;

    private String method;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
