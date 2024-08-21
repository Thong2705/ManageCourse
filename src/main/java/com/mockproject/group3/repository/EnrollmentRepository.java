package com.mockproject.group3.repository;

import com.mockproject.group3.model.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    Optional<Enrollment> findByStudentIdAndCourseId(Integer studentId, Integer courseId);

    Page<Enrollment> findAllByStudentId(Integer studentId, Pageable pageable);

    List<Enrollment> findAllByCourseId(Integer courseId);

    @Query("SELECT COUNT(e) FROM EnrollmentLessonDetail e WHERE e.enrollment.id = :enrollmentId")
    long countCompletedLessonsByEnrollment(@Param("enrollmentId") int enrollmentId);

    @Query("SELECT COUNT(l) FROM Lesson l JOIN l.course c WHERE c.id = :courseId")
    long countTotalLessonsByCourse(@Param("courseId") int courseId);
}
