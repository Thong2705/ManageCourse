package com.mockproject.group3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginGoogleController {
    @GetMapping("/login")
    public String login(){
        return "signin_google";
    }

    @GetMapping("/success")
    public String success(){
        return "success";
    }

    @GetMapping("/check_email")
    public String checkEmail(){
        return "check_email";
    }
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) { 
        model.addAttribute("Pagetitle", "Forgot password");
        return "forgot_password_form";
    }
}
