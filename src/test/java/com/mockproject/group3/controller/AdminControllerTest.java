package com.mockproject.group3.controller;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import com.mockproject.group3.model.Course;

class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @Mock
    private StudentService studentService;

    @Mock
    private InstructorService instructorService;

    @Mock
    private UsersService usersService;

    @Mock
    private GetAuthUserInfo getAuthUserInfo;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllStudent() {
        List<Student> students = Arrays.asList(new Student(), new Student());
        when(studentService.getAllStudent()).thenReturn(students);

        ResponseEntity<List<Student>> response = adminController.getAllStudent();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(students, response.getBody());
        verify(studentService, times(1)).getAllStudent();
    }

    @Test
    void testGetStudentById() {
        Student student = new Student();
        when(studentService.getStudentById(anyInt())).thenReturn(student);

        ResponseEntity<Student> response = adminController.getStudentById(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(student, response.getBody());
        verify(studentService, times(1)).getStudentById(1);
    }

    @Test
    void testGetAllInstructor() {
        List<Instructor> instructors = Arrays.asList(new Instructor(), new Instructor());
        when(instructorService.getAllInstructor()).thenReturn(instructors);

        ResponseEntity<List<Instructor>> response = adminController.getAllInstructor();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(instructors, response.getBody());
        verify(instructorService, times(1)).getAllInstructor();
    }

    @Test
    void testGetAllUsers() {
        List<Users> users = Arrays.asList(new Users(), new Users());
        Page<Users> page = new PageImpl<>(users);
        when(usersService.getAllUsers(any(PaginationParamReq.class))).thenReturn(page);

        PaginationParamReq req = new PaginationParamReq();
        ResponseEntity<BaseApiPaginationRespone<List<Users>>> response = adminController.getAllUsers(req);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(users, response.getBody().getPayload()); // Sử dụng getPayload() để lấy dữ liệu
        verify(usersService, times(1)).getAllUsers(any(PaginationParamReq.class));
    }

    @Test
    void testBlockUsers() {
        Users user = new Users();
        user.setBlocked(false);
        when(adminService.setBlockUsers(anyInt())).thenReturn(user);

        String response = adminController.BlockUsers(1);

        assertEquals("Change thành công", response);
        verify(adminService, times(1)).setBlockUsers(1);
    }

    @Test
    void testSetStatusCourse() {
        Course course = new Course();
        course.setStatus(Status.PENDING);
        when(adminService.setStatusCourse(anyInt(), any(Status.class))).thenReturn(course);

        String response = adminController.setStatusCourse(1, Status.APPROVED);

        assertEquals("Set Status thành công", response);
        verify(adminService, times(1)).setStatusCourse(1, Status.APPROVED);
    }

    @Test
    void testGetAuthUserId() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(123);

        ResponseEntity<Integer> response = adminController.getAuthUserId();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(123, response.getBody());
        verify(getAuthUserInfo, times(1)).getAuthUserId();
    }
}
