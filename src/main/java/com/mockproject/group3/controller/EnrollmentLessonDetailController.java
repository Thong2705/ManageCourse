package com.mockproject.group3.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.dto.response.BaseApiPaginationRespone;
import com.mockproject.group3.model.EnrollmentLessonDetail;
import com.mockproject.group3.service.EnrollmentLessonDetailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/enrollment-lesson-detail")
public class EnrollmentLessonDetailController {

    private final EnrollmentLessonDetailService enrollmentLessonDetailService;

    public EnrollmentLessonDetailController(EnrollmentLessonDetailService enrollmentLessonDetailService) {
        this.enrollmentLessonDetailService = enrollmentLessonDetailService;
    }

    @PostMapping("/course/{courseId}/lesson/{lessonId}")
    public ResponseEntity<?> createEnrollmentLessonDetail(@PathVariable Integer courseId,
            @PathVariable Integer lessonId) {
        EnrollmentLessonDetail enrollmentLessonDetail = enrollmentLessonDetailService
                .createEnrollmentLessonDetail(courseId, lessonId);
        return ResponseEntity.ok(enrollmentLessonDetail);
    }

    @GetMapping
    public ResponseEntity<BaseApiPaginationRespone<List<EnrollmentLessonDetail>>> getAllEnrollmentLessonDetails(
            @Valid @ModelAttribute PaginationParamReq req) {
        Page<EnrollmentLessonDetail> result = enrollmentLessonDetailService.getAllEnrollmentLessonDetails(req);
        return ResponseEntity.ok()
                .body(new BaseApiPaginationRespone<>(0, "Get list enrollment lesson detail successfully",
                        result.toList(),
                        result.getNumber() + 1, result.getSize(), result.getTotalPages(),
                        result.getNumberOfElements()));
    }

    @GetMapping("/self")
    public ResponseEntity<BaseApiPaginationRespone<List<EnrollmentLessonDetail>>> getAllEnrollmentLessonDetailsByStudentId(
            @Valid @ModelAttribute PaginationParamReq req) {
        Page<EnrollmentLessonDetail> result = enrollmentLessonDetailService.getAllByStudentId(req);
        return ResponseEntity.ok()
                .body(new BaseApiPaginationRespone<>(0, "Get your list enrollment lesson detail successfully",
                        result.toList(),
                        result.getNumber() + 1, result.getSize(), result.getTotalPages(),
                        result.getNumberOfElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEnrollmentLessonDetailById(@PathVariable Integer id) {
        return ResponseEntity.ok(enrollmentLessonDetailService.getEnrollmentLessonDetailById(id));
    }

    @GetMapping("/enrollment/{enrollmentId}/lesson/{lessonId}")
    public ResponseEntity<?> getEnrollmentLessonDetailByEnrollmentIdAndLessonId(@PathVariable Integer enrollmentId,
            @PathVariable Integer lessonId) {
        return ResponseEntity.ok(enrollmentLessonDetailService
                .getEnrollmentLessonDetailByEnrollmentIdAndLessonId(enrollmentId, lessonId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEnrollmentLessonDetail(@PathVariable int id) {
        enrollmentLessonDetailService.deleteEnrollmentLessonDetail(id);
        return ResponseEntity.ok("Delete success");
    }

}
