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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    private final GetAuthUserInfo getAuthUserInfo;

    private final StudentRepository studentRepository;

    private final CourseRepository courseRepository;

    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, GetAuthUserInfo getAuthUserInfo,
                         StudentRepository studentRepository, CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.reviewRepository = reviewRepository;
        this.getAuthUserInfo = getAuthUserInfo;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    //Create CRUD methods for Review
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review createReview(ReviewDTO reviewDTO, int courseId) {
        int studentId = getAuthUserInfo.getAuthUserId();
        Student student = studentRepository.findById(studentId).
                orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        Course course = courseRepository.findById(courseId).
                orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_ENROLL_YET));

        //Check if enrollment is finished
        if (enrollmentRepository.countCompletedLessonsByEnrollment(enrollment.getId()) < enrollmentRepository.countTotalLessonsByCourse(courseId)) {
            throw new AppException(ErrorCode.COURSE_NOT_FINISHED);
        }

        Review existing = reviewRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElse(null);

        if (existing != null) {
            throw new AppException(ErrorCode.REVIEW_EXIST);
        }

        Review review = new Review();
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setStudent(student);
        review.setCourse(course);

        return reviewRepository.save(review);
    }

    public Review updateReview(ReviewDTO reviewDTO, int reviewId) {
        int studentId = getAuthUserInfo.getAuthUserId();
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

            if (review.getStudent().getId() != studentId) {
                throw new AppException(ErrorCode.REVIEW_NOT_BELONG_TO_STUDENT);
            }

            review.setRating(reviewDTO.getRating());
            review.setComment(reviewDTO.getComment());

            return reviewRepository.save(review);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR);
        }
    }
}
