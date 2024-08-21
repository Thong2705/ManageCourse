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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class EnrollmentLessonDetailServiceTest {

    @Mock
    private EnrollmentLessonDetailRepository enrollmentLessonDetailRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private GetAuthUserInfo getAuthUserInfo;

    @InjectMocks
    private EnrollmentLessonDetailService enrollmentLessonDetailService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllEnrollmentLessonDetails() {
        PaginationParamReq req = new PaginationParamReq();
        req.setPage(1);
        req.setPageSize(10);

        EnrollmentLessonDetail detail1 = new EnrollmentLessonDetail();
        EnrollmentLessonDetail detail2 = new EnrollmentLessonDetail();
        Page<EnrollmentLessonDetail> page = new PageImpl<>(Arrays.asList(detail1, detail2));

        when(enrollmentLessonDetailRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<EnrollmentLessonDetail> result = enrollmentLessonDetailService.getAllEnrollmentLessonDetails(req);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(enrollmentLessonDetailRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    public void testGetAllByStudentId() {
        PaginationParamReq req = new PaginationParamReq();
        req.setPage(1);
        req.setPageSize(10);

        EnrollmentLessonDetail detail1 = new EnrollmentLessonDetail();
        EnrollmentLessonDetail detail2 = new EnrollmentLessonDetail();
        Page<EnrollmentLessonDetail> page = new PageImpl<>(Arrays.asList(detail1, detail2));

        when(enrollmentLessonDetailRepository.findAllByStudentId(anyInt(), any(PageRequest.class))).thenReturn(page);

        Page<EnrollmentLessonDetail> result = enrollmentLessonDetailService.getAllByStudentId(req);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(enrollmentLessonDetailRepository, times(1)).findAllByStudentId(anyInt(), any(PageRequest.class));
    }

    @Test
    public void testGetEnrollmentLessonDetailById() {
        EnrollmentLessonDetail detail = new EnrollmentLessonDetail();
        when(enrollmentLessonDetailRepository.findById(anyInt())).thenReturn(Optional.of(detail));

        Optional<EnrollmentLessonDetail> result = enrollmentLessonDetailService.getEnrollmentLessonDetailById(1);

        assertTrue(result.isPresent());
        verify(enrollmentLessonDetailRepository, times(1)).findById(anyInt());
    }

    @Test
    public void testGetEnrollmentLessonDetailByEnrollmentIdAndLessonId() {
        EnrollmentLessonDetail detail = new EnrollmentLessonDetail();
        when(enrollmentLessonDetailRepository.findByEnrollmentIdAndLessonId(anyInt(), anyInt())).thenReturn(detail);

        EnrollmentLessonDetail result = enrollmentLessonDetailService
                .getEnrollmentLessonDetailByEnrollmentIdAndLessonId(1, 1);

        assertNotNull(result);
        verify(enrollmentLessonDetailRepository, times(1)).findByEnrollmentIdAndLessonId(anyInt(), anyInt());
    }

    @Test
    public void testCreateEnrollmentLessonDetail() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        Enrollment enrollment = new Enrollment();
        when(enrollmentRepository.findByStudentIdAndCourseId(anyInt(), anyInt())).thenReturn(Optional.of(enrollment));
        Lesson lesson = new Lesson();
        when(lessonRepository.findById(anyInt())).thenReturn(Optional.of(lesson));
        when(lessonRepository.findByCourseId(anyInt())).thenReturn(Collections.singletonList(lesson));
        when(enrollmentLessonDetailRepository.findByEnrollmentIdAndLessonId(anyInt(), anyInt())).thenReturn(null);
        when(enrollmentLessonDetailRepository.save(any(EnrollmentLessonDetail.class)))
                .thenReturn(new EnrollmentLessonDetail());

        EnrollmentLessonDetail result = enrollmentLessonDetailService.createEnrollmentLessonDetail(1, 1);

        assertNotNull(result);
        verify(enrollmentLessonDetailRepository, times(1)).save(any(EnrollmentLessonDetail.class));
    }

    @Test
    public void testCreateEnrollmentLessonDetail_DataIntegrityViolationException() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        Enrollment enrollment = new Enrollment();
        when(enrollmentRepository.findByStudentIdAndCourseId(anyInt(), anyInt())).thenReturn(Optional.of(enrollment));
        Lesson lesson = new Lesson();
        when(lessonRepository.findById(anyInt())).thenReturn(Optional.of(lesson));
        when(lessonRepository.findByCourseId(anyInt())).thenReturn(Collections.singletonList(lesson));
        when(enrollmentLessonDetailRepository.findByEnrollmentIdAndLessonId(anyInt(), anyInt())).thenReturn(null);

        // Mocking the save method to throw DataIntegrityViolationException
        when(enrollmentLessonDetailRepository.save(any(EnrollmentLessonDetail.class)))
                .thenThrow(DataIntegrityViolationException.class);

        AppException exception = assertThrows(AppException.class,
                () -> enrollmentLessonDetailService.createEnrollmentLessonDetail(1, 1));

        assertEquals(ErrorCode.UNKNOWN_ERROR, exception.getErrorCode());
    }

    @Test
    public void testCreateEnrollmentLessonDetail_EnrollmentNotFound() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(enrollmentRepository.findByStudentIdAndCourseId(anyInt(), anyInt())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                () -> enrollmentLessonDetailService.createEnrollmentLessonDetail(1, 1));

        assertEquals(ErrorCode.NOT_ENROLL_YET, exception.getErrorCode());
    }

    @Test
    public void testCreateEnrollmentLessonDetail_LessonNotFound() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        Enrollment enrollment = new Enrollment();
        when(enrollmentRepository.findByStudentIdAndCourseId(anyInt(), anyInt())).thenReturn(Optional.of(enrollment));
        when(lessonRepository.findById(anyInt())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                () -> enrollmentLessonDetailService.createEnrollmentLessonDetail(1, 1));

        assertEquals(ErrorCode.LESSON_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void testCreateEnrollmentLessonDetail_LessonNotInCourse() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        Enrollment enrollment = new Enrollment();
        when(enrollmentRepository.findByStudentIdAndCourseId(anyInt(), anyInt())).thenReturn(Optional.of(enrollment));
        Lesson lesson = new Lesson();
        when(lessonRepository.findById(anyInt())).thenReturn(Optional.of(lesson));
        when(lessonRepository.findByCourseId(anyInt())).thenReturn(Collections.emptyList());

        AppException exception = assertThrows(AppException.class,
                () -> enrollmentLessonDetailService.createEnrollmentLessonDetail(1, 1));

        assertEquals(ErrorCode.LESSON_NOT_IN_COURSE, exception.getErrorCode());
    }

    @Test
    public void testCreateEnrollmentLessonDetail_EnrollmentLessonDetailExist() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        Enrollment enrollment = new Enrollment();
        when(enrollmentRepository.findByStudentIdAndCourseId(anyInt(), anyInt())).thenReturn(Optional.of(enrollment));
        Lesson lesson = new Lesson();
        when(lessonRepository.findById(anyInt())).thenReturn(Optional.of(lesson));
        when(lessonRepository.findByCourseId(anyInt())).thenReturn(Collections.singletonList(lesson));
        EnrollmentLessonDetail existing = new EnrollmentLessonDetail();
        when(enrollmentLessonDetailRepository.findByEnrollmentIdAndLessonId(anyInt(), anyInt())).thenReturn(existing);

        AppException exception = assertThrows(AppException.class,
                () -> enrollmentLessonDetailService.createEnrollmentLessonDetail(1, 1));

        assertEquals(ErrorCode.ENROLLMENT_LESSON_DETAIL_EXIST, exception.getErrorCode());
    }

    @Test
    public void testDeleteEnrollmentLessonDetail() {
        EnrollmentLessonDetail detail = new EnrollmentLessonDetail();
        Lesson lesson = new Lesson();
        detail.setLesson(lesson);
        when(enrollmentLessonDetailRepository.findById(anyInt())).thenReturn(Optional.of(detail));

        doNothing().when(enrollmentLessonDetailRepository).delete(any(EnrollmentLessonDetail.class));

        enrollmentLessonDetailService.deleteEnrollmentLessonDetail(1);

        verify(enrollmentLessonDetailRepository, times(1)).delete(any(EnrollmentLessonDetail.class));
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    public void testDeleteEnrollmentLessonDetail_NotFound() {
        when(enrollmentLessonDetailRepository.findById(anyInt())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                () -> enrollmentLessonDetailService.deleteEnrollmentLessonDetail(1));

        assertEquals(ErrorCode.ENROLLMENT_LESSON_DETAIL_NOTFOUND, exception.getErrorCode());
    }
}
