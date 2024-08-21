package com.mockproject.group3.service;

import com.mockproject.group3.dto.LessonDTO;
import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Lesson;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private LessonService lessonService;

    private Lesson lesson;
    private Course course;
    private LessonDTO lessonDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        lesson = new Lesson();
        lesson.setId(1);
        lesson.setTitle("Lesson Title");
        lesson.setContent("Lesson Content");

        course = new Course();
        course.setId(1);
        lesson.setCourse(course);

        lessonDTO = new LessonDTO();
        lessonDTO.setTitle("Lesson Title");
        lessonDTO.setContent("Lesson Content");
        lessonDTO.setCourseId(1);
    }

    @Test
    void testGetAllLessons() {
        PaginationParamReq req = new PaginationParamReq(1, 10);
        Page<Lesson> page = new PageImpl<>(Collections.singletonList(lesson));
        when(lessonRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Lesson> result = lessonService.getAllLessons(req);

        assertEquals(1, result.getTotalElements());
        verify(lessonRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testGetLessonById() {
        when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));

        Optional<Lesson> result = lessonService.getLessonById(1);

        assertTrue(result.isPresent());
        assertEquals(lesson.getId(), result.get().getId());
        verify(lessonRepository, times(1)).findById(1);
    }

    @Test
    void testGetLessonById_NotFound() {
        when(lessonRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Lesson> result = lessonService.getLessonById(1);

        assertFalse(result.isPresent());
        verify(lessonRepository, times(1)).findById(1);
    }

    @Test
    void testGetLessonByCourseId() {
        when(lessonRepository.findByCourseId(1)).thenReturn(Collections.singletonList(lesson));

        List<Lesson> result = lessonService.getLessonByCourseId(1);

        assertEquals(1, result.size());
        assertEquals(lesson.getId(), result.get(0).getId());
        verify(lessonRepository, times(1)).findByCourseId(1);
    }

    @Test
    void testCreateLesson() {
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

        Lesson result = lessonService.createLesson(lessonDTO);

        assertNotNull(result);
        assertEquals(lesson.getTitle(), result.getTitle());
        verify(courseRepository, times(1)).findById(1);
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void testCreateLesson_CourseNotFound() {
        when(courseRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            lessonService.createLesson(lessonDTO);
        });

        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
        verify(courseRepository, times(1)).findById(1);
        verify(lessonRepository, times(0)).save(any(Lesson.class));
    }

    @Test
    void testUpdateLesson() {
        when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

        Lesson result = lessonService.updateLesson(1, lessonDTO);

        assertNotNull(result);
        assertEquals(lesson.getTitle(), result.getTitle());
        verify(lessonRepository, times(1)).findById(1);
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void testUpdateLesson_NotFound() {
        when(lessonRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            lessonService.updateLesson(1, lessonDTO);
        });

        assertEquals(ErrorCode.LESSON_NOT_FOUND, exception.getErrorCode());
        verify(lessonRepository, times(1)).findById(1);
        verify(lessonRepository, times(0)).save(any(Lesson.class));
    }

    @Test
    void testDeleteLesson() {
        when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));
        doNothing().when(lessonRepository).deleteById(1);

        lessonService.deleteLesson(1);

        verify(lessonRepository, times(1)).findById(1);
        verify(lessonRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteLesson_NotFound() {
        when(lessonRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            lessonService.deleteLesson(1);
        });

        assertEquals(ErrorCode.LESSON_NOT_FOUND, exception.getErrorCode());
        verify(lessonRepository, times(1)).findById(1);
        verify(lessonRepository, times(0)).deleteById(1);
    }
}
