package com.mockproject.group3.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.mockproject.group3.model.Users;
import com.mockproject.group3.repository.UsersRepository;
import com.mockproject.group3.service.UsersService;

import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
public class UsersControllerTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private UsersService usersService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private UsersController usersController;

    @BeforeEach
    void setUp() {
        usersController = new UsersController(usersRepository, usersService);
    }

    @Test
    void testShowRegistrationForm() {
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("name", "John Doe");
        userAttributes.put("email", "john.doe@example.com");

        when(session.getAttribute("userAttributes")).thenReturn(userAttributes);

        String viewName = usersController.showRegistrationForm(session, model);

        assertEquals("register", viewName);
        verify(model).addAttribute("name", "John Doe");
        verify(model).addAttribute("email", "john.doe@example.com");
    }

    @Test
    void testShowRegistrationFormNoAttributes() {
        when(session.getAttribute("userAttributes")).thenReturn(null);

        String viewName = usersController.showRegistrationForm(session, model);

        assertEquals("register", viewName);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void testVerifyUserSuccess() {
        Users user = mock(Users.class);
        when(usersRepository.findByVerificationCode("validCode")).thenReturn(user);

        String viewName = usersController.verifyUser("validCode", model);

        assertEquals("verification_success", viewName);
        verify(user).setVerified(true);
        verify(user).setVerificationCode(null);
        verify(usersRepository).save(user);
        verify(model).addAttribute("message", "Your account has been successfully verified.");
    }

    @Test
    void testVerifyUserFailure() {
        when(usersRepository.findByVerificationCode("invalidCode")).thenReturn(null);

        String viewName = usersController.verifyUser("invalidCode", model);

        assertEquals("verification_failed", viewName);
        verify(model).addAttribute("message", "Invalid verification code");
    }
}
