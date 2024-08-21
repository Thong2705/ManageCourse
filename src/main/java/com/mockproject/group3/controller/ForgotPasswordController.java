package com.mockproject.group3.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mockproject.group3.model.Users;
import com.mockproject.group3.service.EmailService;
import com.mockproject.group3.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;



@Controller
@RequestMapping("forgot-password")
public class ForgotPasswordController {
    private final  UsersService usersService;
    private final EmailService emailService;

    public ForgotPasswordController(UsersService usersService, EmailService emailService) {
        this.usersService = usersService;
        this.emailService = emailService;
    }

    @PostMapping("/check-forgot-password")
    public String processForgotPasswordForm (HttpServletRequest request, Model model){
        String email = request.getParameter("email");
        String token = usersService.RandomString(16);

        
        try {
            usersService.updateResetPasswordToken(token, email);
            String resetPasswordLink = EmailService.getLinkResetPassword(request) + "/forgot-password/reset-password?token=" + token;
            
            String subject = "RESET PASSWORD";
            String content = "<p>Click the link below to reset your password: </p>"
                        + "<p><a href=\"" + resetPasswordLink + "\">Change your password</a></p>";
    
            emailService.sendEmailVerify(email, subject, content);
            model.addAttribute("message", "We have sent a reset password link to your email. Please check." );
        } catch (Exception e) {
            model.addAttribute("error" , e.getMessage());
        }


        return "forgot_password_form";
    }
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        Users user = usersService.getUserByTokenResetPassword(token);
        if (user == null) {
            model.addAttribute("title","Reset your password");
            model.addAttribute("message", "Invalid verification code");
            return "verification_failed";
        }

        model.addAttribute("token", token);
        model.addAttribute("pageTitle","Reset your password");

        return "new_password_form";
    }
    
    @PostMapping("/reset-password")
    public String ResetPassword(HttpServletRequest request, Model model ) {
        
        String token = request.getParameter("token");
        String passsword = request.getParameter("password");
        Users user = usersService.getUserByTokenResetPassword(token);
        if (user == null){
            model.addAttribute("title", "Reset your password");
            model.addAttribute("message", "Invalid token");
            return "message";
        } else {
            usersService.updateNewPassword(user, passsword);
            model.addAttribute("message", "you have changed successfully");

        }
        return "checktochangepassword";
        
    }

}
