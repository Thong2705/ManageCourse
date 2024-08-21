package com.mockproject.group3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mockproject.group3.model.Instructor;
import com.mockproject.group3.model.Review;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
    @Query("SELECT r FROM Review r JOIN r.course c JOIN c.instructor i WHERE i.id = :instructorId AND c.id = :courseId")
    List<Review> findReviewsByInstructorAndCourseId(@Param("instructorId") int instructorId, @Param("courseId") int courseId);
}
