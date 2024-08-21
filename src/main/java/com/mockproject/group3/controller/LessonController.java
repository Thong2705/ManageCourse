package com.mockproject.group3.controller;

import com.mockproject.group3.dto.LessonDTO;
import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.dto.response.BaseApiPaginationRespone;
import com.mockproject.group3.dto.response.course.CourseWithRatingRes;
import com.mockproject.group3.model.Lesson;
import com.mockproject.group3.service.LessonService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {
    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping
    public ResponseEntity<?> createLesson(@RequestBody LessonDTO lessonDTO) {
        Lesson lesson = lessonService.createLesson(lessonDTO);
        return ResponseEntity.ok(lesson);
    }

    @GetMapping
    public ResponseEntity<BaseApiPaginationRespone<List<Lesson>>> getAllLessons(@Valid @ModelAttribute PaginationParamReq req) {

        Page<Lesson> result = lessonService.getAllLessons(req);
        return ResponseEntity.ok()
                .body(new BaseApiPaginationRespone<>(0, "Get list lesson successfully",
                        result.toList(),
                        result.getNumber() + 1, result.getSize(), result.getTotalPages(),
                        result.getNumberOfElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Integer id) {
        Optional<Lesson> lesson = lessonService.getLessonById(id);
        return lesson.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Lesson>> getLessonsByCourseId(@PathVariable Integer courseId) {
        List<Lesson> lessons = lessonService.getLessonByCourseId(courseId);
        return ResponseEntity.ok(lessons);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLesson(@PathVariable Integer id, @RequestBody LessonDTO lessonDTO) {
        Lesson lesson = lessonService.updateLesson(id, lessonDTO);
        return ResponseEntity.ok(lesson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable Integer id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok().build();
    }
}
