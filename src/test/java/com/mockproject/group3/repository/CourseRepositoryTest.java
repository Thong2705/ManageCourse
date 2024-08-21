package com.mockproject.group3.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.mockproject.group3.dto.response.course.CourseWithRatingRes;
import com.mockproject.group3.dto.response.instructor.EarningInstructorRes;
import com.mockproject.group3.enums.Status;
import com.mockproject.group3.model.Course;

@SpringBootTest
public class CourseRepositoryTest {
    @Mock
    private CourseRepository courseRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindCourseOtherVersion() {
        List<Course> courses = List.of(new Course());
        when(courseRepository.findCourseOtherVersion(anyString())).thenReturn(courses);

        List<Course> result = courseRepository.findCourseOtherVersion("testCode");
        assertFalse(result.isEmpty());
    }

    @Test
    public void testFindCourseOriginalVersion() {
        Course course = new Course();
        when(courseRepository.findCourseOriginalVersion(anyString())).thenReturn(course);

        Course result = courseRepository.findCourseOriginalVersion("testCode");
        assertNotNull(result);
    }

    @Test
    public void testEarningAnalytics() {
        List<EarningInstructorRes> earnings = List.of(new EarningInstructorRes());
        when(courseRepository.earningAnalytics(any(LocalDateTime.class), any(LocalDateTime.class), anyInt()))
                .thenReturn(earnings);

        List<EarningInstructorRes> result = courseRepository.earningAnalytics(LocalDateTime.now(), LocalDateTime.now(),
                1);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testFindByIdAndInstructorId() {
        Course course = new Course();
        when(courseRepository.findByIdAndInstructorId(anyInt(), anyInt())).thenReturn(Optional.of(course));

        Optional<Course> result = courseRepository.findByIdAndInstructorId(1, 1);
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindByKeywordWithCategoryOnlyActivate() {
        List<CourseWithRatingRes> courses = List.of(new CourseWithRatingRes());
        Page<CourseWithRatingRes> coursePage = new PageImpl<>(courses);
        PageRequest pageable = PageRequest.of(0, 10);

        when(courseRepository.findByKeywordWithCategoryOnlyActivate(anyString(), anyInt(), eq(pageable)))
                .thenReturn(coursePage);

        Page<CourseWithRatingRes> result = courseRepository.findByKeywordWithCategoryOnlyActivate("keyword", 1,
                pageable);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testFindByKeywordOnlyActivate() {
        List<CourseWithRatingRes> courses = List.of(new CourseWithRatingRes());
        Page<CourseWithRatingRes> coursePage = new PageImpl<>(courses);
        PageRequest pageable = PageRequest.of(0, 10);

        when(courseRepository.findByKeywordOnlyActivate(anyString(), eq(pageable))).thenReturn(coursePage);

        Page<CourseWithRatingRes> result = courseRepository.findByKeywordOnlyActivate("keyword", pageable);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testFindCourseByInstructor() {
        List<Course> courses = List.of(new Course());
        Page<Course> coursePage = new PageImpl<>(courses);
        PageRequest pageable = PageRequest.of(0, 10);

        when(courseRepository.findCourseByInstructor(anyString(), anyInt(), eq(pageable))).thenReturn(coursePage);

        Page<Course> result = courseRepository.findCourseByInstructor("keyword", 1, pageable);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testFindByTitleAndStatus() {
        List<Course> courses = List.of(new Course());
        Page<Course> coursePage = new PageImpl<>(courses);
        PageRequest pageable = PageRequest.of(0, 10);

        when(courseRepository.findByTitleAndStatus(anyString(), any(Status.class), eq(pageable)))
                .thenReturn(coursePage);

        Page<Course> result = courseRepository.findByTitleAndStatus("title", Status.APPROVED, pageable);
        assertFalse(result.isEmpty());
    }

}
