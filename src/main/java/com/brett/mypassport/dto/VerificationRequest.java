package com.brett.mypassport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for sending verification code")
public class VerificationRequest {

    @Schema(description = "Email address to receive the verification code", example = "user@example.com")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
