package com.mockproject.group3.controller;

import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.mockproject.group3.dto.UsersDTO;
import com.mockproject.group3.enums.Role;
import com.mockproject.group3.model.Users;
import com.mockproject.group3.repository.UsersRepository;
import com.mockproject.group3.service.EmailService;
import com.mockproject.group3.service.InstructorService;
import com.mockproject.group3.service.StudentService;


@Controller
@RequestMapping("/register_google")
public class RegisterGoogleController {
    private final EmailService emailService;
    private final UsersRepository usersRepository;
    private final InstructorService instructorService;
    private final StudentService studentService;
    

    public RegisterGoogleController(EmailService emailService, UsersRepository usersRepository,
                                    InstructorService instructorService, StudentService studentService) {
        this.emailService = emailService;
        this.usersRepository = usersRepository;
        this.instructorService = instructorService;
        this.studentService = studentService;
    }


    @PostMapping("/register_instructor")
    public String registerInstructor(@RequestParam String name,
    @RequestParam String email,
    @RequestParam String password,
    @RequestParam String address,
    @RequestParam String phone) {
        UsersDTO userDto = new UsersDTO();
        userDto.setFull_name(name);
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setAddress(address);
        userDto.setPhone(phone);
        userDto.setRole(Role.STUDENT);
        userDto.setIsBlocked(false);
        String verificationCode = UUID.randomUUID().toString();
        userDto.setVerificationCode(verificationCode);

        // Gọi phương thức saveStudent từ StudentService
        instructorService.saveInstructor(userDto);
        sendVerificationEmail(userDto);
        return "redirect:/login/check_email";
    }


    @PostMapping("/register_student")
    public String registerStudent(@RequestParam String name,
    @RequestParam String email,
    @RequestParam String password,
    @RequestParam String address,
    @RequestParam String phone) {
        UsersDTO userDto = new UsersDTO();
        userDto.setFull_name(name);
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setAddress(address);
        userDto.setPhone(phone);
        userDto.setRole(Role.STUDENT);
        userDto.setIsBlocked(false);
        String verificationCode = UUID.randomUUID().toString();
        userDto.setVerificationCode(verificationCode);


        // Gọi phương thức saveStudent từ StudentService
        studentService.saveStudent(userDto);
        sendVerificationEmail(userDto);
        return "redirect:/login/check_email";
    }



    private void sendVerificationEmail(UsersDTO user) {
        String subject = "Please verify your registration";
        String verificationUrl = "http://localhost:8080/register_google/verify?code=" + user.getVerificationCode();
        String message = "Please click the link below to verify your registration:\n" + verificationUrl;
    
        emailService.sendEmailVerify(user.getEmail(), subject, message);
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
