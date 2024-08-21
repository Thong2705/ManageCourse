package com.mockproject.group3.controller;

import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Enrollment;
import com.mockproject.group3.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentControllerTest {

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentController enrollmentController;

    private MockMvc mockMvc;
    private Enrollment enrollment;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(enrollmentController).build();
        enrollment = new Enrollment();
        enrollment.setId(1);
        enrollment.setDescription("Test Enrollment");
    }

    @Test
    void testCreateEnrollment() throws Exception {
        when(enrollmentService.createEnrollment(1)).thenReturn(enrollment);

        mockMvc.perform(post("/api/enrollments/student/course/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Enrollment"));
    }


    @Test
    void testGetEnrollmentById() throws Exception {
        when(enrollmentService.getEnrollmentById(1)).thenReturn(Optional.of(enrollment));

        mockMvc.perform(get("/api/enrollments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Enrollment"));
    }

    @Test
    void testGetEnrollmentByIdNotFound() throws Exception {
        when(enrollmentService.getEnrollmentById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/enrollments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetEnrollmentByStudentIdAndCourseId() throws Exception {
        when(enrollmentService.getEnrollmentByStudentIdAndCourseId(1, 1)).thenReturn(enrollment);

        mockMvc.perform(get("/api/enrollments/student/1/course/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Test Enrollment"));
    }

    @Test
    void getAllLessonByEnrollmentId() throws Exception {
        Course course = new Course();
        course.setId(1);
        enrollment.setCourse(course);

        when(enrollmentService.getEnrollmentById(1)).thenReturn(Optional.of(enrollment));

        mockMvc.perform(get("/api/enrollments/1/review")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testViewTrackingCourse() throws Exception {
        Course course = new Course();
        course.setId(1);
        enrollment.setCourse(course);

        when(enrollmentService.getEnrollmentById(1)).thenReturn(Optional.of(enrollment));
        when(enrollmentService.getProcess(1, 1)).thenReturn(0.5);

        mockMvc.perform(get("/api/enrollments/1/view-tracking-course")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("0.5"));
    }

    @Test
    public void testGetAllEnrollments() throws Exception {
        // Mock data setup
        List<Enrollment> enrollments = Collections.singletonList(new Enrollment());
        Page<Enrollment> page = new PageImpl<>(enrollments, PageRequest.of(0, 10), 1);

        // Mocking the service method
        when(enrollmentService.getAllEnrollments(any(PaginationParamReq.class))).thenReturn(page);

        // Perform the request and assert the results
        mockMvc.perform(get("/api/enrollments")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Get list enrollment successfully"))
                .andExpect(jsonPath("$.payload.length()").value(enrollments.size()))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalPage").value(1))
                .andExpect(jsonPath("$.totalItem").value(1));
    }

    @Test
    void testGetAllEnrollmentsByStudentId() throws Exception {
        // Arrange
        PaginationParamReq req = new PaginationParamReq();
        req.setPage(1);
        req.setPageSize(10);

        Enrollment enrollment = new Enrollment();
        enrollment.setId(1);
        enrollment.setDescription("Test Enrollment");

        List<Enrollment> enrollments = Collections.singletonList(enrollment);
        Page<Enrollment> page = new PageImpl<>(enrollments, PageRequest.of(0, 10), 1);

        when(enrollmentService.getAllEnrollmentsByStudentId(any(PaginationParamReq.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/enrollments/student")
                        .param("page", "1")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Get list enrollment successfully"))
                .andExpect(jsonPath("$.payload.length()").value(enrollments.size()))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalPage").value(1))
                .andExpect(jsonPath("$.totalItem").value(1));
    }

}
