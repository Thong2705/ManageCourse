package com.mockproject.group3.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mockproject.group3.model.Users;
import com.mockproject.group3.service.EmailService;
import com.mockproject.group3.service.UsersService;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsersService usersService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ForgotPasswordController forgotPasswordController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(forgotPasswordController).build();
    }

    @Test
    void testProcessForgotPasswordForm_Success() throws Exception {
        String email = "test@example.com";
        String token = "randomToken";

        when(usersService.RandomString(16)).thenReturn(token);
        doNothing().when(usersService).updateResetPasswordToken(token, email);
        doNothing().when(emailService).sendEmailVerify(any(String.class), any(String.class), any(String.class));

        mockMvc.perform(post("/forgot-password/check-forgot-password")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(model().attribute("message", "We have sent a reset password link to your email. Please check."))
                .andExpect(view().name("forgot_password_form"));

        verify(usersService).updateResetPasswordToken(token, email);
        verify(emailService).sendEmailVerify(any(String.class), any(String.class), any(String.class));
    }

    @Test
    void testProcessForgotPasswordForm_Error() throws Exception {
        String email = "test@example.com";
        String token = "randomToken";

        when(usersService.RandomString(16)).thenReturn(token);
        doThrow(new RuntimeException("Error updating token")).when(usersService).updateResetPasswordToken(token, email);

        mockMvc.perform(post("/forgot-password/check-forgot-password")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "Error updating token"))
                .andExpect(view().name("forgot_password_form"));

        verify(usersService).updateResetPasswordToken(token, email);
    }

    @Test
    void testShowResetPasswordForm_ValidToken() throws Exception {
        String token = "validToken";
        Users user = new Users(); // Assuming Users is a simple POJO

        when(usersService.getUserByTokenResetPassword(token)).thenReturn(user);

        mockMvc.perform(get("/forgot-password/reset-password").param("token", token))
                .andExpect(status().isOk())
                .andExpect(model().attribute("token", token))
                .andExpect(model().attribute("pageTitle", "Reset your password"))
                .andExpect(view().name("new_password_form"));

        verify(usersService).getUserByTokenResetPassword(token);
    }

    @Test
    void testShowResetPasswordForm_InvalidToken() throws Exception {
        String token = "invalidToken";

        when(usersService.getUserByTokenResetPassword(token)).thenReturn(null);

        mockMvc.perform(get("/forgot-password/reset-password").param("token", token))
                .andExpect(status().isOk())
                .andExpect(model().attribute("title", "Reset your password"))
                .andExpect(model().attribute("message", "Invalid verification code"))
                .andExpect(view().name("verification_failed"));

        verify(usersService).getUserByTokenResetPassword(token);
    }

    @Test
    void testResetPassword_Success() throws Exception {
        String token = "validToken";
        String password = "newPassword";
        Users user = new Users(); // Assuming Users is a simple POJO

        when(usersService.getUserByTokenResetPassword(token)).thenReturn(user);
        doNothing().when(usersService).updateNewPassword(user, password);

        mockMvc.perform(post("/forgot-password/reset-password")
                .param("token", token)
                .param("password", password))
                .andExpect(status().isOk())
                .andExpect(model().attribute("message", "you have changed successfully"))
                .andExpect(view().name("checktochangepassword"));

        verify(usersService).updateNewPassword(user, password);
    }

    @Test
    void testResetPassword_InvalidToken() throws Exception {
        String token = "invalidToken";
        String password = "newPassword";

        when(usersService.getUserByTokenResetPassword(token)).thenReturn(null);

        mockMvc.perform(post("/forgot-password/reset-password")
                .param("token", token)
                .param("password", password))
                .andExpect(status().isOk())
                .andExpect(model().attribute("title", "Reset your password"))
                .andExpect(model().attribute("message", "Invalid token"))
                .andExpect(view().name("message"));

        verify(usersService).getUserByTokenResetPassword(token);
    }
}
