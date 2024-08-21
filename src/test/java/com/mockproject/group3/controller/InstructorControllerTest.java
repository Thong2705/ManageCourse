package com.mockproject.group3.controller;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.mockproject.group3.dto.ChangePasswordDTO;
import com.mockproject.group3.dto.InstructorDTO;
import com.mockproject.group3.dto.UsersDTO;
import com.mockproject.group3.dto.request.instructor.EarningReq;
import com.mockproject.group3.dto.response.BaseApiResponse;
import com.mockproject.group3.dto.response.instructor.EarningInstructorRes;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Instructor;
import com.mockproject.group3.model.Review;
import com.mockproject.group3.service.InstructorService;
import com.mockproject.group3.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class InstructorControllerTest {

    @Mock
    private InstructorService instructorService;

    @Mock
    private UsersService usersService;

    @InjectMocks
    private InstructorController instructorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRefreshToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(usersService.refreshToken(request, response)).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> result = instructorController.refreshToken(request, response);

        assertEquals(200, result.getStatusCodeValue());
        verify(usersService, times(1)).refreshToken(request, response);
    }

    @Test
    void testChangePassword() {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        when(instructorService.changePassword(any(ChangePasswordDTO.class))).thenReturn(true);

        ResponseEntity<?> result = instructorController.changePassword(changePasswordDTO);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Đổi mật khẩu thành công", result.getBody());        
        verify(instructorService, times(1)).changePassword(changePasswordDTO);
    }

    @Test
    void testChangePasswordException() {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        when(instructorService.changePassword(any(ChangePasswordDTO.class))).thenThrow(new RuntimeException());

        AppException exception = assertThrows(AppException.class, () -> {
            instructorController.changePassword(changePasswordDTO);
        });

        assertEquals(ErrorCode.UNKNOWN_ERROR, exception.getErrorCode());
        verify(instructorService, times(1)).changePassword(changePasswordDTO);
    }

    @Test
    void testUpdateProfile() {
        UsersDTO usersDTO = new UsersDTO();
        Instructor instructor = new Instructor();
        when(instructorService.updateProfile(any(UsersDTO.class))).thenReturn(instructor);

        ResponseEntity<?> response = instructorController.updateProfile(usersDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(instructor, response.getBody());
        verify(instructorService, times(1)).updateProfile(usersDTO);
    }

    @Test
    void testUpdateProfileException() {
        UsersDTO usersDTO = new UsersDTO();
        when(instructorService.updateProfile(any(UsersDTO.class))).thenThrow(new RuntimeException());

        AppException exception = assertThrows(AppException.class, () -> {
            instructorController.updateProfile(usersDTO);
        });

        assertEquals(ErrorCode.UNKNOWN_ERROR, exception.getErrorCode());
        verify(instructorService, times(1)).updateProfile(usersDTO);
    }

    @Test
    void testGetEarningAnalytics() {
        EarningReq earningReq = new EarningReq();
        List<EarningInstructorRes> earnings = Arrays.asList(new EarningInstructorRes(), new EarningInstructorRes());
        when(instructorService.viewEarning(any(EarningReq.class))).thenReturn(earnings);

        ResponseEntity<BaseApiResponse<List<EarningInstructorRes>>> response = instructorController.getEarningAnalytics(earningReq);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(earnings, response.getBody().getPayload());
        verify(instructorService, times(1)).viewEarning(any(EarningReq.class));
    }

    @Test
    void testGetReviewsByInstructorAndCourse() {
        List<Review> reviews = Arrays.asList(new Review(), new Review());
        when(instructorService.findReviewsByInstructorAndCourseId(anyInt())).thenReturn(reviews);

        ResponseEntity<List<Review>> response = instructorController.getReviewsByInstructorAndCourse(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(reviews, response.getBody());
        verify(instructorService, times(1)).findReviewsByInstructorAndCourseId(1);
    }

    @Test
    void testSearchInstructor() {
        List<InstructorDTO> instructors = Arrays.asList(new InstructorDTO(), new InstructorDTO());
        when(instructorService.searchInstructorDTO(anyString())).thenReturn(instructors);

        ResponseEntity<List<InstructorDTO>> response = instructorController.searchInstructor("name");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(instructors, response.getBody());
        verify(instructorService, times(1)).searchInstructorDTO("name");
    }
}
