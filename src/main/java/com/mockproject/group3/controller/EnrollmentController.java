package com.mockproject.group3.controller;

import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.dto.response.BaseApiPaginationRespone;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Lesson;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Enrollment;
import com.mockproject.group3.service.EnrollmentService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/student/course/{courseId}")
    public ResponseEntity<?> createEnrollment(@Valid @PathVariable Integer courseId) {
            Enrollment enrollment = enrollmentService.createEnrollment(courseId);
            return ResponseEntity.ok(enrollment);
    }

    @GetMapping
    public ResponseEntity<BaseApiPaginationRespone<List<Enrollment>>> getAllEnrollments(@Valid @ModelAttribute PaginationParamReq req) {
        Page<Enrollment> result = enrollmentService.getAllEnrollments(req);
        return ResponseEntity.ok()
                .body(new BaseApiPaginationRespone<>(0, "Get list enrollment successfully",
                        result.toList(),
                        result.getNumber() + 1, result.getSize(), result.getTotalPages(),
                        result.getNumberOfElements()));
    }

    @GetMapping("/student")
    public ResponseEntity<BaseApiPaginationRespone<List<Enrollment>>> getAllEnrollmentsByStudentId(@Valid @ModelAttribute PaginationParamReq req) {
        Page<Enrollment> result = enrollmentService.getAllEnrollmentsByStudentId(req);
        return ResponseEntity.ok()
                .body(new BaseApiPaginationRespone<>(0, "Get list enrollment successfully",
                        result.toList(),
                        result.getNumber() + 1, result.getSize(), result.getTotalPages(),
                        result.getNumberOfElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> getEnrollmentById(@PathVariable Integer id) {
        Optional<Enrollment> enrollment = enrollmentService.getEnrollmentById(id);
        return enrollment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<Enrollment> getEnrollmentByStudentIdAndCourseId(@PathVariable Integer studentId, @PathVariable Integer courseId) {
        return ResponseEntity.ok().body(enrollmentService.getEnrollmentByStudentIdAndCourseId(studentId, courseId));
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/{enrollmentId}/review")
    public ResponseEntity<?> getAllLessonByEnrollmentId(@PathVariable Integer enrollmentId) {
        Enrollment enrollment = enrollmentService.getEnrollmentById(enrollmentId).orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        Course course = enrollment.getCourse();
        Set<Lesson> lessons = course.getLessons();
        return ResponseEntity.ok(lessons);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/{enrollmentId}/view-tracking-course")
    public ResponseEntity<?> viewTrackingCourse(@PathVariable Integer enrollmentId) {
        Enrollment enrollment = enrollmentService.getEnrollmentById(enrollmentId).orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        Course course = enrollment.getCourse();

        double progress = enrollmentService.getProcess(enrollmentId, course.getId());

        return ResponseEntity.ok(progress);
    }
}
