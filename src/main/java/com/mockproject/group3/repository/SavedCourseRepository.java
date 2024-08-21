package com.mockproject.group3.repository;

import com.mockproject.group3.model.SavedCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavedCourseRepository extends JpaRepository<SavedCourse, Integer> {

    Optional<SavedCourse> findByCourseIdAndStudentId(int courseId, int studentId);
}
