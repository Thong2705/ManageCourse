package com.mockproject.group3.controller;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.dto.response.BaseApiPaginationRespone;
import com.mockproject.group3.enums.Status;
import com.mockproject.group3.model.Instructor;
import com.mockproject.group3.model.Student;
import com.mockproject.group3.model.Users;
import com.mockproject.group3.service.AdminService;
import com.mockproject.group3.service.InstructorService;
import com.mockproject.group3.service.StudentService;
import com.mockproject.group3.service.UsersService;
import com.mockproject.group3.utils.GetAuthUserInfo;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final StudentService studentService;
    private final InstructorService instructorService;
    private final UsersService usersService;
    private final GetAuthUserInfo getAuthUserInfo;
    
    public AdminController(AdminService adminService, StudentService studentService,
            InstructorService instructorService, @Lazy UsersService usersService, GetAuthUserInfo getAuthUserInfo) {
        this.adminService = adminService;
        this.studentService = studentService;
        this.instructorService = instructorService;
        this.usersService = usersService;
        this.getAuthUserInfo = getAuthUserInfo;
    }

    @GetMapping("/student")
    public ResponseEntity<List<Student>> getAllStudent() {
        List<Student> students = studentService.getAllStudent();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping("/instructor")
    public ResponseEntity<List<Instructor>> getAllInstructor() {
        List<Instructor> instructors = instructorService.getAllInstructor();
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/users")
    public ResponseEntity<BaseApiPaginationRespone<List<Users>>> getAllUsers(@Valid @ModelAttribute PaginationParamReq req) {
        Page<Users> result = usersService.getAllUsers(req);
        return ResponseEntity.ok()
                .body(new BaseApiPaginationRespone<>(0, "Get list enrollment successfully",
                        result.toList(),
                        result.getNumber() + 1, result.getSize(), result.getTotalPages(),
                        result.getNumberOfElements()));
    }

    @PutMapping("/setBlock/{id}")
    public String BlockUsers(@PathVariable int id) {
        adminService.setBlockUsers(id);
        return "Change thành công";
        
    }
    @PutMapping("/setStatusCourse/{id}")
    public String setStatusCourse(@PathVariable int id, @RequestBody Status status) {
        adminService.setStatusCourse(id, status); 
        return "Set Status thành công";  
    }

    @GetMapping("/demo")
    public ResponseEntity<Integer> getAuthUserId() {
        int userId = getAuthUserInfo.getAuthUserId();
        return ResponseEntity.ok(userId);
    }
}
