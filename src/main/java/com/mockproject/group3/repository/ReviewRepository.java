package com.mockproject.group3.repository;

import com.mockproject.group3.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Optional<Review> findByStudentIdAndCourseId(Integer studentId, Integer courseId);
}
