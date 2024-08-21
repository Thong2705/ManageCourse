package com.mockproject.group3.repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mockproject.group3.dto.response.course.CourseWithRatingRes;
import com.mockproject.group3.dto.response.instructor.EarningInstructorRes;
import com.mockproject.group3.enums.Status;
import com.mockproject.group3.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

        Optional<Course> findByIdAndInstructorId(int id, int instructorId);

//        @Query("SELECT c FROM Course c WHERE c.status = 'APPROVED' AND c.version = 1")
//        List<Course> GetAllByInstructorId(int instructorId);

        @Query("SELECT c FROM Course c WHERE c.version <> 1 AND c.codeCourse = :code")
        List<Course> findCourseOtherVersion(@Param("code") String codeCourse);

        @Query("SELECT c FROM Course c WHERE c.version = 1 AND c.codeCourse = :code")
        Course findCourseOriginalVersion(@Param("code") String codeCourse);

        @Query("SELECT new com.mockproject.group3.dto.response.course.CourseWithRatingRes(c, COALESCE(AVG(f.rating), 0.0) as avarageRating) "
                        +
                        "FROM Course c " +
                        "LEFT JOIN c.reviews f " +
                        "WHERE ( c.title LIKE %:keyword% OR c.instructor.user.full_name LIKE%:keyword%) AND c.category.id=:categoryId AND c.status='APPROVED' "
                        +
                        "GROUP BY c.id " +
                        "ORDER BY avarageRating DESC")
        Page<CourseWithRatingRes> findByKeywordWithCategoryOnlyActivate(@Param("keyword") String keyword,
                        @Param("categoryId") int categoryId, Pageable pageable);

        @Query("SELECT new com.mockproject.group3.dto.response.course.CourseWithRatingRes(c, COALESCE(AVG(f.rating), 0.0) as avarageRating) "
                        +
                        "FROM Course c " +
                        "LEFT JOIN c.reviews f " +
                        "WHERE ( c.title LIKE %:keyword% OR c.instructor.user.full_name LIKE %:keyword% ) AND c.status = 'APPROVED' "
                        +
                        "GROUP BY c.id " +
                        "ORDER BY avarageRating DESC")
        Page<CourseWithRatingRes> findByKeywordOnlyActivate(@Param("keyword") String keyword, Pageable pageable);

        @Query("SELECT c FROM Course c WHERE c.title LIKE %:keyword% AND c.instructor.id = :instructor_id " +
                        "AND c.version = (" +
                        "   SELECT MAX(c2.version) " +
                        "   FROM Course c2 " +
                        "   WHERE c2.codeCourse = c.codeCourse " +
                        "   AND c2.instructor.id = c.instructor.id" +
                        ")")
        Page<Course> findCourseByInstructor(@Param("keyword") String keyword, @Param("instructor_id") int instructorId,
                        Pageable pageable);

        @Query("SELECT c FROM Course c WHERE c.title LIKE %:title% " +
                        "AND (:status IS NULL OR c.status = :status) " +
                        "AND c.version = (" +
                        "   SELECT MAX(c2.version) " +
                        "   FROM Course c2 " +
                        "   WHERE c2.codeCourse = c.codeCourse " +
                        ")")
        Page<Course> findByTitleAndStatus(@Param("title") String title, @Param("status") Status status,
                        Pageable pageable);

        @Query("SELECT new com.mockproject.group3.dto.response.instructor.EarningInstructorRes(c.id, c.title, COUNT(*), SUM(c.price)) FROM Course c LEFT JOIN c.paymentDetails pd LEFT JOIN pd.payment p WHERE p.payment_date BETWEEN :fromDay AND :toDay AND c.instructor.id = :id GROUP BY c.id, c.title")
        List<EarningInstructorRes> earningAnalytics(@Param("fromDay") LocalDateTime fromDay,
                        @Param("toDay") LocalDateTime toDay, @Param("id") int id);
}
