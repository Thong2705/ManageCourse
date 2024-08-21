package com.mockproject.group3.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mockproject.group3.dto.request.course.AdminCourseParamReq;
import com.mockproject.group3.dto.request.course.CourseParamReq;
import com.mockproject.group3.dto.request.course.CreateCourseReq;
import com.mockproject.group3.dto.request.course.InstructorCourseParamReq;
import com.mockproject.group3.dto.request.course.ReviewCourseReq;
import com.mockproject.group3.dto.request.course.SubmitCourseReq;
import com.mockproject.group3.dto.request.course.UpdateCourseReq;
import com.mockproject.group3.dto.response.BaseApiPaginationRespone;
import com.mockproject.group3.dto.response.BaseApiResponse;
import com.mockproject.group3.dto.response.course.CourseWithRatingRes;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.service.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/course")
public class CourseController {
    private CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/student")
    public ResponseEntity<BaseApiPaginationRespone<List<CourseWithRatingRes>>> getAllCourseForStudent(
            @Valid @ModelAttribute CourseParamReq req) {
        Page<CourseWithRatingRes> result = courseService.getAllForStudent(req);

        return ResponseEntity.ok()
                .body(new BaseApiPaginationRespone<List<CourseWithRatingRes>>(0, "Get list course succesfully",
                        result.toList(),
                        result.getNumber() + 1, result.getSize(), result.getTotalPages(),
                        result.getNumberOfElements()));
    }

    @GetMapping("/instructor")
    public ResponseEntity<BaseApiPaginationRespone<List<Course>>> getAllCourseForInstructor(
            @Valid @ModelAttribute InstructorCourseParamReq req) {
        Page<Course> result = courseService.getAllForInstructor(req);
        return ResponseEntity.ok()
                .body(new BaseApiPaginationRespone<List<Course>>(0, "Get list course succesfully", result.toList(),
                        result.getNumber() + 1, result.getSize(), result.getTotalPages(),
                        result.getNumberOfElements()));
    }

    @PostMapping("/submit")
    public ResponseEntity<BaseApiResponse<Void>> submitCourse(@Valid @RequestBody SubmitCourseReq req) {
        courseService.submitCourse(req);
        return ResponseEntity.ok().body(new BaseApiResponse<>(0, "submit course successfully"));
    }

    @PostMapping()
    public ResponseEntity<BaseApiResponse<Course>> createCourse(@RequestBody @Valid CreateCourseReq req) {
        var newCourse = courseService.create(req);
        return ResponseEntity.ok().body(new BaseApiResponse<Course>(0, "create course successfully", newCourse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseApiResponse<Course>> updateCourse(@PathVariable int id,
            @RequestBody @Valid UpdateCourseReq req) {

        Course updatedCourse = courseService.update(id, req);
        return ResponseEntity.ok().body(new BaseApiResponse<Course>(0, "update course successfully", updatedCourse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseApiResponse<Void>> deleteCourse(@PathVariable int id) {
        courseService.delete(id);
        return ResponseEntity.ok().body(new BaseApiResponse<>(0, "delete course successfully"));
    }

    @GetMapping("/admin")
    public ResponseEntity<BaseApiPaginationRespone<List<Course>>> getAllCourseForAdmin(
            @Valid @ModelAttribute AdminCourseParamReq req) {
        Page<Course> result = courseService.getAllForAdmin(req);
        return ResponseEntity.ok()
                .body(new BaseApiPaginationRespone<List<Course>>(0, "Get list course succesfully", result.toList(),
                        result.getNumber() + 1, result.getSize(), result.getTotalPages(),
                        result.getNumberOfElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseApiResponse<Course>> getCourse(@PathVariable int id) {
        return ResponseEntity.ok()
                .body(new BaseApiResponse<Course>(0, "get course successfully", courseService.getOneById(id)));
    }

    @PostMapping("/admin/review")
    public ResponseEntity<BaseApiResponse<Void>> reviewCourse(@RequestBody @Valid ReviewCourseReq req) {
        courseService.reviewCourse(req);
        return ResponseEntity.ok()
                .body(new BaseApiResponse<>(0, "Review end. Status of course changed to " + req.getStatus().name()));
    }
}
