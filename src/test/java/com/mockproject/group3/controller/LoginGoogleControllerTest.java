package com.mockproject.group3.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
public class LoginGoogleControllerTest {

    @Mock
    private Model model;

    @InjectMocks
    private LoginGoogleController loginGoogleController;

    @BeforeEach
    void setUp() {
        loginGoogleController = new LoginGoogleController();
    }

    @Test
    void testLogin() {
        String viewName = loginGoogleController.login();
        assertEquals("signin_google", viewName);
    }

    @Test
    void testSuccess() {
        String viewName = loginGoogleController.success();
        assertEquals("success", viewName);
    }

    @Test
    void testCheckEmail() {
        String viewName = loginGoogleController.checkEmail();
        assertEquals("check_email", viewName);
    }

    @Test
    void testShowForgotPasswordForm() {
        String viewName = loginGoogleController.showForgotPasswordForm(model);
        assertEquals("forgot_password_form", viewName);
        verify(model).addAttribute("Pagetitle", "Forgot password");
    }
}
