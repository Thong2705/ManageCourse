package com.mockproject.group3.dto;

import jakarta.validation.constraints.NotBlank;

public class ChangePasswordDTO {
    @NotBlank(message = "Yêu cầu nhập password hiện tại")
    private String currentPassword;

    @NotBlank(message = "Yêu cầu nhập password mới")
    private String newPassword;

    @NotBlank(message = "Yêu cầu xác nhận lại mật khẩu")
    private String confirmPassword;

    // Getters and Setters

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
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
