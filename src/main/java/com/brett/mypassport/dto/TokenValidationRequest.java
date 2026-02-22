package com.brett.mypassport.dto;

import jakarta.validation.constraints.NotBlank;

public class TokenValidationRequest {
    @NotBlank(message = "Token cannot be empty")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
