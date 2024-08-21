package com.mockproject.group3.controller;

import com.mockproject.group3.dto.SavedCourseDTO;
import com.mockproject.group3.model.SavedCourse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mockproject.group3.service.SavedCourseService;

import java.util.List;

@RestController
@RequestMapping("/api/saved-course")
public class SavedCourseController {

    private final SavedCourseService savedCourseService;

    public SavedCourseController(SavedCourseService savedCourseService) {
        this.savedCourseService = savedCourseService;
    }

    @PostMapping("/student")
    public ResponseEntity<SavedCourse> createSavedCourse(@RequestBody SavedCourseDTO savedCourseDTO) {
        SavedCourse savedCourse = savedCourseService.createSavedCourse(savedCourseDTO);
        return ResponseEntity.ok(savedCourse);
    }

    @GetMapping("/student")
    public ResponseEntity<List<SavedCourse>> getAllSavedCourse() {
        List<SavedCourse> savedCourse = savedCourseService.getAllSavedCourse();
        return ResponseEntity.ok(savedCourse);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSavedCourse(@PathVariable int id) {
        try {
            savedCourseService.deleteSavedCourse(id);
            return ResponseEntity.ok("Delete success");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
