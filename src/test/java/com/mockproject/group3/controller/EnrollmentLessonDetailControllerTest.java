package com.mockproject.group3.controller;

import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.dto.response.BaseApiPaginationRespone;
import com.mockproject.group3.model.EnrollmentLessonDetail;
import com.mockproject.group3.service.EnrollmentLessonDetailService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class EnrollmentLessonDetailControllerTest {

    @Mock
    private EnrollmentLessonDetailService enrollmentLessonDetailService;

    @InjectMocks
    private EnrollmentLessonDetailController enrollmentLessonDetailController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateEnrollmentLessonDetail() {
        EnrollmentLessonDetail enrollmentLessonDetail = new EnrollmentLessonDetail();
        when(enrollmentLessonDetailService.createEnrollmentLessonDetail(anyInt(), anyInt()))
                .thenReturn(enrollmentLessonDetail);

        ResponseEntity<?> response = enrollmentLessonDetailController.createEnrollmentLessonDetail(1, 1);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(enrollmentLessonDetail, response.getBody());
        verify(enrollmentLessonDetailService, times(1)).createEnrollmentLessonDetail(anyInt(), anyInt());
    }

    @Test
    void testGetAllEnrollmentLessonDetails() {
        PaginationParamReq req = new PaginationParamReq();
        req.setPage(1);
        req.setPageSize(10);

        EnrollmentLessonDetail detail1 = new EnrollmentLessonDetail();
        EnrollmentLessonDetail detail2 = new EnrollmentLessonDetail();
        Page<EnrollmentLessonDetail> page = new PageImpl<>(Arrays.asList(detail1, detail2));

        when(enrollmentLessonDetailService.getAllEnrollmentLessonDetails(any(PaginationParamReq.class)))
                .thenReturn(page);

        ResponseEntity<BaseApiPaginationRespone<List<EnrollmentLessonDetail>>> response = enrollmentLessonDetailController
                .getAllEnrollmentLessonDetails(req);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getPayload().size());
        verify(enrollmentLessonDetailService, times(1)).getAllEnrollmentLessonDetails(any(PaginationParamReq.class));
    }

    @Test
    void testGetAllByStudentId() {
        PaginationParamReq req = new PaginationParamReq();
        req.setPage(1);
        req.setPageSize(10);

        EnrollmentLessonDetail detail1 = new EnrollmentLessonDetail();
        EnrollmentLessonDetail detail2 = new EnrollmentLessonDetail();
        Page<EnrollmentLessonDetail> page = new PageImpl<>(Arrays.asList(detail1, detail2));

        when(enrollmentLessonDetailService.getAllByStudentId(any(PaginationParamReq.class))).thenReturn(page);

        ResponseEntity<BaseApiPaginationRespone<List<EnrollmentLessonDetail>>> response = enrollmentLessonDetailController
                .getAllEnrollmentLessonDetailsByStudentId(req);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getCode());
        assertEquals("Get your list enrollment lesson detail successfully", response.getBody().getMessage());
        verify(enrollmentLessonDetailService, times(1)).getAllByStudentId(any(PaginationParamReq.class));
    }

    @Test
    void testGetEnrollmentLessonDetailById() {
        EnrollmentLessonDetail detail = new EnrollmentLessonDetail();
        when(enrollmentLessonDetailService.getEnrollmentLessonDetailById(anyInt())).thenReturn(Optional.of(detail));

        ResponseEntity<?> response = enrollmentLessonDetailController.getEnrollmentLessonDetailById(1);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof Optional);
        assertEquals(Optional.of(detail), response.getBody());
        verify(enrollmentLessonDetailService, times(1)).getEnrollmentLessonDetailById(anyInt());
    }

    @Test
    void testGetEnrollmentLessonDetailByEnrollmentIdAndLessonId() {
        EnrollmentLessonDetail detail = new EnrollmentLessonDetail();
        when(enrollmentLessonDetailService.getEnrollmentLessonDetailByEnrollmentIdAndLessonId(anyInt(), anyInt()))
                .thenReturn(detail);

        ResponseEntity<?> response = enrollmentLessonDetailController
                .getEnrollmentLessonDetailByEnrollmentIdAndLessonId(1, 1);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(detail, response.getBody());
        verify(enrollmentLessonDetailService, times(1)).getEnrollmentLessonDetailByEnrollmentIdAndLessonId(anyInt(),
                anyInt());
    }

    @Test
    void testDeleteEnrollmentLessonDetail() {
        doNothing().when(enrollmentLessonDetailService).deleteEnrollmentLessonDetail(anyInt());

        ResponseEntity<?> response = enrollmentLessonDetailController.deleteEnrollmentLessonDetail(1);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Delete success", response.getBody());
        verify(enrollmentLessonDetailService, times(1)).deleteEnrollmentLessonDetail(anyInt());
    }
}
