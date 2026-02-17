package com.brett.mypassport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for changing password")
public class ChangePasswordRequest {

    @Schema(description = "Current password", example = "OldPassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;

    @Schema(description = "New password", example = "NewPassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;

    @Schema(description = "Confirmation of new password", example = "NewPassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
