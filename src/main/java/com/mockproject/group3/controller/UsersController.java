package com.mockproject.group3.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mockproject.group3.model.Users;
import com.mockproject.group3.repository.UsersRepository;
import com.mockproject.group3.service.EmailService;
import com.mockproject.group3.service.UsersService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UsersController {

    private final UsersRepository usersRepository;
    private EmailService emailService;
    private final UsersService usersService;
    public UsersController(UsersRepository usersRepository, UsersService usersService) {
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(HttpSession session, Model model) {
        Map<String, Object> userAttributes = (Map<String, Object>) session.getAttribute("userAttributes");

        if (userAttributes != null) {
            model.addAttribute("name", userAttributes.get("name"));
            model.addAttribute("email", userAttributes.get("email"));
        }

        return "register";
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam String code, Model model) {
        Users user = usersRepository.findByVerificationCode(code);

        if (user == null) {
            model.addAttribute("message", "Invalid verification code");
            return "verification_failed";
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        usersRepository.save(user);

        model.addAttribute("message", "Your account has been successfully verified.");
        return "verification_success";
    }
    


    
}
