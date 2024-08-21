package com.mockproject.group3.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mockproject.group3.dto.ChangePasswordDTO;
import com.mockproject.group3.dto.UsersDTO;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Student;
import com.mockproject.group3.service.StudentService;
import com.mockproject.group3.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;
    private final UsersService usersService;    
    
    public StudentController(StudentService studentService, UsersService usersService) {
        this.studentService = studentService;
        this.usersService = usersService;
    }

//    @PostMapping("/register")
//    public ResponseEntity<AuthenticationResponse> createStudent(@Valid @RequestBody UsersDTO userDto) {
//        AuthenticationResponse authResponse = studentService.saveStudent(userDto);
//
//        return ResponseEntity.ok(authResponse);
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody UsersDTO userDto) {
//        return ResponseEntity.ok(usersService.authenticate(userDto));
//    }

    @PostMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return usersService.refreshToken(request, response);
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
        studentService.changePassword(changePasswordDTO); 
        return ResponseEntity.ok("Đổi mật khẩu thành công");
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR);
        }
    }
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UsersDTO userDto){
        try {
            Student student = studentService.updateProfile(userDto);
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR);
        }

    }

}
