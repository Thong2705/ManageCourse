package com.mockproject.group3.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mockproject.group3.enums.Status;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Token;
import com.mockproject.group3.model.Users;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.UsersRepository;

import io.jsonwebtoken.Jwts;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private AdminService adminService;

    @Captor
    private ArgumentCaptor<Users> usersCaptor;

    @Captor
    private ArgumentCaptor<Course> courseCaptor;

    private Users user;
    private Course course;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId(1);
        user.setBlocked(false);

        course = new Course();
        course.setId(1);
        course.setStatus(Status.CREATED);
    }

    @Test
    void testSetBlockUsers_UserNotFound() {
        when(usersRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            adminService.setBlockUsers(1);
        });

        assertEquals(ErrorCode.USER_NOTFOUND, exception.getErrorCode());

        verify(usersRepository).findById(1);
        verify(usersRepository, never()).save(any(Users.class));
    }

    @Test
    void testSetBlockUsers_BlockUser() {
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(usersRepository.save(any(Users.class))).thenReturn(user); 
        user.setBlocked(false);  // User is initially not blocked

        Users result = adminService.setBlockUsers(1);

        assertEquals(true, result.isBlocked());

        verify(usersRepository).findById(1);
        verify(usersRepository).save(usersCaptor.capture());
        Users savedUser = usersCaptor.getValue();
        assertEquals(true, savedUser.isBlocked());
    }

    @Test
    void testSetBlockUsers_UnblockUser() {
        when(usersRepository.findById(1)).thenReturn(Optional.of(user));
        when(usersRepository.save(any(Users.class))).thenReturn(user); 
        user.setBlocked(true);  // User is initially blocked

        Users result = adminService.setBlockUsers(1);

        assertEquals(false, result.isBlocked());

        verify(usersRepository).findById(1);
        verify(usersRepository).save(usersCaptor.capture());
        Users savedUser = usersCaptor.getValue();
        assertEquals(false, savedUser.isBlocked());
    }

    @Test
    void testSetStatusCourse_CourseNotFound() {
        when(courseRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            adminService.setStatusCourse(1, Status.REJECTED);
        });

        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());

        verify(courseRepository).findById(1);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void testSetStatusCourse_Success() {
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);  // Ensure the save method returns the course object

        Course result = adminService.setStatusCourse(1, Status.REJECTED);

        assertEquals(Status.REJECTED, result.getStatus());

        verify(courseRepository).findById(1);
        verify(courseRepository).save(courseCaptor.capture());
        Course savedCourse = courseCaptor.getValue();
        assertEquals(Status.REJECTED, savedCourse.getStatus());
    }

}
