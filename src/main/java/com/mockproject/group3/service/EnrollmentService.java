package com.mockproject.group3.service;

import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.enums.EnrollmentStatus;
import com.mockproject.group3.enums.Status;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Enrollment;
import com.mockproject.group3.model.Payment;
import com.mockproject.group3.model.Student;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.EnrollmentRepository;
import com.mockproject.group3.repository.PaymentRepository;
import com.mockproject.group3.repository.StudentRepository;
import com.mockproject.group3.utils.GetAuthUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("hasAuthority('STUDENT')")
public class EnrollmentService {


    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PaymentRepository paymentRepository;

    private final GetAuthUserInfo getAuthUserInfo;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository, CourseRepository courseRepository, PaymentRepository paymentRepository,GetAuthUserInfo getAuthUserInfo) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.paymentRepository = paymentRepository;
        this.getAuthUserInfo = getAuthUserInfo;
    }

    public Page<Enrollment> getAllEnrollments(PaginationParamReq req) {
        PageRequest pageRequest = PageRequest.of(
                Math.max(req.getPage() - 1, 0),
                req.getPageSize(),
                Sort.by("id").descending()
        );

        return enrollmentRepository.findAll(pageRequest);
    }

    public Page<Enrollment> getAllEnrollmentsByStudentId(PaginationParamReq req) {
        PageRequest pageRequest = PageRequest.of(
                Math.max(req.getPage() - 1, 0),
                req.getPageSize(),
                Sort.by("id").descending()
        );

        return enrollmentRepository.findAllByStudentId(getAuthUserInfo.getAuthUserId(), pageRequest);
    }

    public Optional<Enrollment> getEnrollmentById(Integer id) {
        return enrollmentRepository.findById(id);
    }

    public Enrollment getEnrollmentByStudentIdAndCourseId(Integer studentId, Integer courseId) {
        return enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
    }

    public Enrollment createEnrollment(Integer courseId) {
        int userId = getAuthUserInfo.getAuthUserId();
        try {
            Student student = studentRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
            Enrollment existing = enrollmentRepository
                    .findByStudentIdAndCourseId(userId, courseId).orElse(null);
                    System.out.println(existing);
            List<Payment> payments = paymentRepository.findByStudentIdAndStatus(userId, Status.APPROVED);
//            boolean hasPaidForCourse = payments.stream()
//                    .flatMap(payment -> payment.getPaymentDetails().stream())
//                    .anyMatch(paymentDetail -> paymentDetail.getCourses().getId() == courseId);
            boolean hasPaidForCourse = payments.stream()
                    .flatMap(payment -> Optional.ofNullable(payment.getPaymentDetails()).orElse(Collections.emptySet()).stream())
                    .anyMatch(paymentDetail -> paymentDetail.getCourses().getId() == courseId);

            if (!hasPaidForCourse) {
                throw new AppException(ErrorCode.COURSE_NOT_PURCHASED);
            }

            if (existing != null)
                throw new AppException(ErrorCode.ENROLLMENT_EXIST);
            Enrollment enrollment = new Enrollment();
            enrollment.setDescription("Student " + userId + " enrolled in course " + courseId);
            enrollment.setStatus(EnrollmentStatus.ENROLLED);
            enrollment.setStudent(student);
            enrollment.setCourse(course);

            return enrollmentRepository.save(enrollment);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public double getProcess(int enrollmentId, int courseId) {
        long completedLessons = enrollmentRepository.countCompletedLessonsByEnrollment(enrollmentId);
        long totalLessons = enrollmentRepository.countTotalLessonsByCourse(courseId);
        return totalLessons > 0 ? (double) completedLessons / totalLessons : 0.0;
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public double getCompletedLessonRate(int courseId) {
        long totalStudent = enrollmentRepository.findAllByCourseId(courseId).size();
        System.out.println("total student: " + totalStudent);
        long studentCompleted = 0;
        for(Enrollment enrollment : enrollmentRepository.findAllByCourseId(courseId)){
            System.out.println("enrollment id: " + enrollment.getId());
            System.out.print("process: " + getProcess(enrollment.getId(), courseId));
            if(getProcess(enrollment.getId(), courseId) == 1.0){
                studentCompleted++;
            }
        }
        System.out.println("student complete: " + studentCompleted);
        return totalStudent > 0 ? (double) studentCompleted / totalStudent * 100 : 0.0;
    }
//    public Enrollment updateEnrollment(Integer id, EnrollmentDTO enrollmentDTO) {
//        try {
//            Optional<Enrollment> existingEnrollment = enrollmentRepository.findById(id);
//            if (existingEnrollment.isPresent()) {
//                Enrollment enrollment = existingEnrollment.get();
//
//                Enrollment existing = enrollmentRepository.findByStudentIdAndCourseId(enrollmentDTO.getStudentId(), enrollmentDTO.getCourseId());
//                if (existing != null && existing.getId() != id) {
//                    throw new RuntimeException("Enrollment with the same studentId and courseId already exists");
//                }
//
//                Student student = studentRepository.findById(enrollmentDTO.getStudentId())
//                        .orElseThrow(() -> new RuntimeException("Student not found"));
//                Course course = courseRepository.findById(enrollmentDTO.getCourseId())
//                        .orElseThrow(() -> new RuntimeException("Course not found"));
//
//                enrollment.setDescription(enrollmentDTO.getDescription());
//                enrollment.setStatus(enrollmentDTO.getStatus());
//                enrollment.setStudent(student);
//                enrollment.setCourse(course);
//
//                return enrollmentRepository.save(enrollment);
//            } else {
//                throw new RuntimeException("Enrollment not found");
//            }
//        } catch (DataIntegrityViolationException e) {
//            throw new RuntimeException("Enrollment not found");
//        }
//    }
}
