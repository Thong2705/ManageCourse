package com.mockproject.group3.controller;

import com.mockproject.group3.dto.UsersDTO;
import com.mockproject.group3.model.AuthenticationResponse;
import com.mockproject.group3.service.InstructorService;
import com.mockproject.group3.service.StudentService;
import com.mockproject.group3.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validation")
public class ValidationController {
    private final UsersService usersService;
    private final StudentService studentService;
    private final InstructorService instructorService;

    public ValidationController(UsersService usersService, StudentService studentService, InstructorService instructorService) {
        this.usersService = usersService;
        this.studentService = studentService;
        this.instructorService = instructorService;
    }

    @PostMapping("/student_register")
    public ResponseEntity<AuthenticationResponse> createStudent(@Valid @RequestBody UsersDTO userDto) {
        AuthenticationResponse authResponse = studentService.saveStudent(userDto);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/instructor_register")
    public ResponseEntity<AuthenticationResponse> createInstructor(@Valid @RequestBody UsersDTO userDto) {
        AuthenticationResponse authResponse = instructorService.saveInstructor(userDto);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody UsersDTO userDto) {
        return ResponseEntity.ok(usersService.authenticate(userDto));
    }
}
