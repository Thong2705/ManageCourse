package com.mockproject.group3.service;

import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Enrollment;
import com.mockproject.group3.model.EnrollmentLessonDetail;
import com.mockproject.group3.model.Lesson;
import com.mockproject.group3.repository.EnrollmentLessonDetailRepository;
import com.mockproject.group3.repository.EnrollmentRepository;
import com.mockproject.group3.repository.LessonRepository;
import com.mockproject.group3.utils.GetAuthUserInfo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("hasAuthority('STUDENT')")
public class EnrollmentLessonDetailService {
    private final EnrollmentLessonDetailRepository enrollmentLessonDetailRepository;

    private final EnrollmentRepository enrollmentRepository;

    private final LessonRepository lessonRepository;

    private final GetAuthUserInfo getAuthUserInfo;

    public EnrollmentLessonDetailService(EnrollmentLessonDetailRepository enrollmentLessonDetailRepository,
            EnrollmentRepository enrollmentRepository, LessonRepository lessonRepository,
            GetAuthUserInfo getAuthUserInfo) {
        this.enrollmentLessonDetailRepository = enrollmentLessonDetailRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonRepository = lessonRepository;
        this.getAuthUserInfo = getAuthUserInfo;
    }

    public Page<EnrollmentLessonDetail> getAllEnrollmentLessonDetails(PaginationParamReq req) {
        PageRequest pageRequest = PageRequest.of(
                Math.max(req.getPage() - 1, 0),
                req.getPageSize(),
                Sort.by("id").descending());

        return enrollmentLessonDetailRepository.findAll(pageRequest);
    }

    public Page<EnrollmentLessonDetail> getAllByStudentId(PaginationParamReq req) {
        PageRequest pageRequest = PageRequest.of(
                Math.max(req.getPage() - 1, 0),
                req.getPageSize(),
                Sort.by("id").ascending());
        int userId = getAuthUserInfo.getAuthUserId();
        return enrollmentLessonDetailRepository.findAllByStudentId(userId, pageRequest);
    }

    public Optional<EnrollmentLessonDetail> getEnrollmentLessonDetailById(Integer id) {
        return enrollmentLessonDetailRepository.findById(id);
    }

    public EnrollmentLessonDetail getEnrollmentLessonDetailByEnrollmentIdAndLessonId(Integer enrollmentId,
            Integer lessonId) {
        return enrollmentLessonDetailRepository.findByEnrollmentIdAndLessonId(enrollmentId, lessonId);
    }

    public EnrollmentLessonDetail createEnrollmentLessonDetail(int courseId, int lessonId) {
        int studentId = getAuthUserInfo.getAuthUserId();
        try {
            Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_ENROLL_YET));
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
            // Lấy danh sách Lesson của Course
            List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
            // Kiểm tra Lesson có tồn tại trong Course không
            if (!lessons.contains(lesson)) {
                throw new AppException(ErrorCode.LESSON_NOT_IN_COURSE);
            }

            EnrollmentLessonDetail existing = enrollmentLessonDetailRepository
                    .findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId);
            if (existing != null) {
                throw new AppException(ErrorCode.ENROLLMENT_LESSON_DETAIL_EXIST);
            }

            EnrollmentLessonDetail enrollmentLessonDetail = new EnrollmentLessonDetail();
            enrollmentLessonDetail.setEnrollment(enrollment);
            enrollmentLessonDetail.setLesson(lesson);

            return enrollmentLessonDetailRepository.save(enrollmentLessonDetail);

        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    @Transactional
    public void deleteEnrollmentLessonDetail(int enrollmentLessonDetailId) {
        EnrollmentLessonDetail enrollmentLessonDetail = enrollmentLessonDetailRepository
                .findById(enrollmentLessonDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_LESSON_DETAIL_NOTFOUND));

        // Lấy Lesson liên quan
        Lesson lesson = enrollmentLessonDetail.getLesson();

        // Xóa EnrollmentLessonDetail
        enrollmentLessonDetailRepository.delete(enrollmentLessonDetail);

        // Cập nhật status của Lesson
        updateLessonStatus(lesson);
    }

    private void updateLessonStatus(Lesson lesson) {
        lessonRepository.save(lesson);
    }
    // public EnrollmentLessonDetail updateEnrollmentLessonDetail(Integer id,
    // EnrollmentLessonDetailDTO enrollmentLessonDetailDTO) {
    // try {
    // Optional<EnrollmentLessonDetail> existingEnrollmentLessonDetail =
    // enrollmentLessonDetailRepository.findById(id);
    // if (existingEnrollmentLessonDetail.isPresent()) {
    // EnrollmentLessonDetail enrollmentLessonDetail =
    // existingEnrollmentLessonDetail.get();
    //
    // EnrollmentLessonDetail existing =
    // enrollmentLessonDetailRepository.findByEnrollmentIdAndLessonId(enrollmentLessonDetailDTO.getEnrollmentId(),
    // enrollmentLessonDetailDTO.getLessonId());
    //
    // if (existing != null) {
    // throw new RuntimeException("EnrollmentLessonDetail already exists");
    // }
    // Enrollment enrollment =
    // enrollmentRepository.findById(enrollmentLessonDetailDTO.getEnrollmentId())
    // .orElseThrow(() -> new RuntimeException("Enrollment not found"));
    // Lesson lesson =
    // lessonRepository.findById(enrollmentLessonDetailDTO.getLessonId())
    // .orElseThrow(() -> new RuntimeException("Lesson not found"));
    //
    // enrollmentLessonDetail.setEnrollment(enrollment);
    // enrollmentLessonDetail.setLesson(lesson);
    //
    // return enrollmentLessonDetailRepository.save(enrollmentLessonDetail);
    // }else{
    // throw new RuntimeException("EnrollmentLessonDetail not found");
    // }
    // }catch (DataIntegrityViolationException e){
    // throw new RuntimeException("EnrollmentLessonDetail not found");
    // }
    // }

}
