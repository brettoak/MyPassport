package com.brett.mypassport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for resetting password")
public class ResetPasswordRequest {

    @Schema(description = "Email address of the user", example = "user@example.com")
    private String email;

    @Schema(description = "Verification code received via email", example = "123456")
    private String verificationCode;

    @Schema(description = "New password", example = "NewPassword123!")
    private String newPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
