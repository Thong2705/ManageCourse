package com.mockproject.group3.service;

import com.mockproject.group3.dto.ReviewDTO;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Enrollment;
import com.mockproject.group3.model.Review;
import com.mockproject.group3.model.Student;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.EnrollmentRepository;
import com.mockproject.group3.repository.ReviewRepository;
import com.mockproject.group3.repository.StudentRepository;
import com.mockproject.group3.utils.GetAuthUserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private GetAuthUserInfo getAuthUserInfo;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewDTO reviewDTO;
    private Student student;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        reviewDTO = new ReviewDTO();
        reviewDTO.setRating(5);
        reviewDTO.setComment("Great course!");

        student = new Student();
        student.setId(1);

        course = new Course();
        course.setId(1);

        enrollment = new Enrollment();
        enrollment.setId(1);
        enrollment.setStudent(student);
        enrollment.setCourse(course);
    }

    @Test
    void testGetAllReviews() {
        List<Review> reviews = new ArrayList<>();
        when(reviewRepository.findAll()).thenReturn(reviews);

        List<Review> result = reviewService.getAllReviews();
        assertNotNull(result);
        assertEquals(reviews, result);
    }

    @Test
    void testCreateReviewSuccess() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudentIdAndCourseId(1, 1)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.countCompletedLessonsByEnrollment(1)).thenReturn((long) 10);
        when(enrollmentRepository.countTotalLessonsByCourse(1)).thenReturn((long) 10);
        when(reviewRepository.findByStudentIdAndCourseId(1, 1)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(new Review());

        Review result = reviewService.createReview(reviewDTO, 1);
        assertNotNull(result);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testCreateReviewCourseNotFinished() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudentIdAndCourseId(1, 1)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.countCompletedLessonsByEnrollment(1)).thenReturn(5L);
        when(enrollmentRepository.countTotalLessonsByCourse(1)).thenReturn(10L);

        AppException exception = assertThrows(AppException.class, () -> reviewService.createReview(reviewDTO, 1));
        assertEquals(ErrorCode.COURSE_NOT_FINISHED, exception.getErrorCode());
    }

    @Test
    void testCreateReviewStudentNotFound() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> reviewService.createReview(reviewDTO, 1));
        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testCreateReviewCourseNotFound() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> reviewService.createReview(reviewDTO, 1));
        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testUpdateReviewSuccess() {
        Review review = new Review();
        review.setId(1);
        review.setStudent(student);
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(reviewRepository.findById(1)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review result = reviewService.updateReview(reviewDTO, 1);
        assertNotNull(result);
        assertEquals(reviewDTO.getRating(), result.getRating());
        assertEquals(reviewDTO.getComment(), result.getComment());
    }

    @Test
    void testUpdateReviewNotFound() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(reviewRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> reviewService.updateReview(reviewDTO, 1));
        assertEquals(ErrorCode.UNKNOWN_ERROR, exception.getErrorCode());
    }

    @Test
    void testUpdateReviewNotBelongToStudent() {
        Review review = new Review();
        review.setId(1);
        Student anotherStudent = new Student();
        anotherStudent.setId(2);
        review.setStudent(anotherStudent);
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(reviewRepository.findById(1)).thenReturn(Optional.of(review));

        AppException exception = assertThrows(AppException.class, () -> reviewService.updateReview(reviewDTO, 1));
        assertEquals(ErrorCode.UNKNOWN_ERROR, exception.getErrorCode());
    }
}
