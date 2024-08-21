package com.mockproject.group3.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.mockproject.group3.dto.ChangePasswordDTO;
import com.mockproject.group3.dto.UsersDTO;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Student;
import com.mockproject.group3.service.StudentService;
import com.mockproject.group3.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @Mock
    private UsersService usersService;

    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRefreshToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(usersService.refreshToken(request, response)).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> result = studentController.refreshToken(request, response);

        assertEquals(200, result.getStatusCodeValue());
        verify(usersService, times(1)).refreshToken(request, response);
    }

    @Test
    void testChangePassword() {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        when(studentService.changePassword(any(ChangePasswordDTO.class))).thenReturn(true);

        ResponseEntity<?> result = studentController.changePassword(changePasswordDTO);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Đổi mật khẩu thành công", result.getBody());
        verify(studentService, times(1)).changePassword(changePasswordDTO);
    }

    @Test
    void testChangePasswordException() {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        when(studentService.changePassword(any(ChangePasswordDTO.class))).thenThrow(new RuntimeException());

        AppException exception = assertThrows(AppException.class, () -> {
            studentController.changePassword(changePasswordDTO);
        });

        assertEquals(ErrorCode.UNKNOWN_ERROR, exception.getErrorCode());
        verify(studentService, times(1)).changePassword(changePasswordDTO);
    }

    @Test
    void testUpdateProfile() {
        UsersDTO usersDTO = new UsersDTO();
        Student student = new Student();
        when(studentService.updateProfile(any(UsersDTO.class))).thenReturn(student);

        ResponseEntity<?> response = studentController.updateProfile(usersDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(student, response.getBody());
        verify(studentService, times(1)).updateProfile(usersDTO);
    }

    @Test
    void testUpdateProfileException() {
        UsersDTO usersDTO = new UsersDTO();
        when(studentService.updateProfile(any(UsersDTO.class))).thenThrow(new RuntimeException());

        AppException exception = assertThrows(AppException.class, () -> {
            studentController.updateProfile(usersDTO);
        });

        assertEquals(ErrorCode.UNKNOWN_ERROR, exception.getErrorCode());
        verify(studentService, times(1)).updateProfile(usersDTO);
    }
}
