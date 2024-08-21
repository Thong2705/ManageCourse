package com.mockproject.group3.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mockproject.group3.model.EnrollmentLessonDetail;

public interface EnrollmentLessonDetailRepository extends JpaRepository<EnrollmentLessonDetail, Integer> {
    EnrollmentLessonDetail findByEnrollmentIdAndLessonId(Integer enrollmentId, Integer lessonId);

    boolean existsByLessonId(int lessonId);

    @Query("SELECT e FROM EnrollmentLessonDetail e WHERE e.enrollment.student.id = :studentId")
    Page<EnrollmentLessonDetail> findAllByStudentId(@Param("studentId") int studentId, Pageable pageable);
}
