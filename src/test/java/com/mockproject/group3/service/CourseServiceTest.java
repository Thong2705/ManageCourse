package com.mockproject.group3.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.mockproject.group3.dto.request.course.AdminCourseParamReq;
import com.mockproject.group3.dto.request.course.CourseParamReq;
import com.mockproject.group3.dto.request.course.CreateCourseReq;
import com.mockproject.group3.dto.request.course.InstructorCourseParamReq;
import com.mockproject.group3.dto.request.course.ReviewCourseReq;
import com.mockproject.group3.dto.request.course.SubmitCourseReq;
import com.mockproject.group3.dto.request.course.UpdateCourseReq;
import com.mockproject.group3.dto.request.lesson.CreateLessonReq;
import com.mockproject.group3.dto.request.lesson.UpdateLessonReq;
import com.mockproject.group3.dto.response.course.CourseWithRatingRes;
import com.mockproject.group3.enums.Status;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Category;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Instructor;
import com.mockproject.group3.model.Lesson;
import com.mockproject.group3.repository.CategoryRepository;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.InstructorRepository;
import com.mockproject.group3.repository.LessonRepository;
import com.mockproject.group3.utils.GetAuthUserInfo;

@SpringBootTest
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private GetAuthUserInfo getAuthUserInfo;

    @InjectMocks
    private CourseService courseService;

    private CourseParamReq req;

    private Instructor instructor;
    private Category category;
    private Course course;
    private Course courseSaved;
    private UpdateCourseReq reqUpdate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        req = new CourseParamReq();
        req.setPage(1);
        req.setPageSize(10);
        req.setKeyword("Java");
        req.setSortBy(new String[] {});
        req.setSortDirection(new String[] {});
        req.setCategoryId(0);

        Course course1 = new Course();
        course1.setTitle("Java 101");

        Course course2 = new Course();
        course2.setTitle("Java Advanced");

        instructor = new Instructor();
        instructor.setId(1);

        category = new Category();
        category.setId(1);

        course = new Course();
        course.setId(1);
        course.setInstructor(instructor);
        course.setCategory(category);

        instructor = new Instructor();
        instructor.setId(1);
        instructor.setFee(100.0);

        category = new Category();
        category.setId(1);

        course = new Course();
        course.setId(1);
        course.setTitle("Test Course");
        course.setStatus(Status.CREATED);
        course.setInstructor(instructor);
        course.setCategory(category);
        course.setCodeCourse("COURSE_CODE");

        courseSaved = new Course();
        courseSaved.setId(2);
        courseSaved.setTitle("New Course");
        courseSaved.setStatus(Status.CREATED);
        courseSaved.setInstructor(instructor);
        courseSaved.setCategory(category);
        courseSaved.setCodeCourse("COURSE_CODE");

        reqUpdate = new UpdateCourseReq();
        reqUpdate.setTitle("Updated Course");
        reqUpdate.setDescription("Updated Description");
        reqUpdate.setPrice(200.0);
        reqUpdate.setCategoryId(1);

        UpdateLessonReq lessonReq = new UpdateLessonReq();
        lessonReq.setId(0);
        lessonReq.setTitle("Updated Lesson");
        lessonReq.setContent("Updated Content");

        List<UpdateLessonReq> lessons = new ArrayList<>();
        lessons.add(lessonReq);
        reqUpdate.setLessons(lessons);
    }

    @Test
    public void testUpdateCourse_Success() {
        int courseId = 1;
        int userId = 1;

        UpdateLessonReq lessonReq1 = new UpdateLessonReq("Lesson 1", "Content 1", 1);
        UpdateLessonReq lessonReq2 = new UpdateLessonReq("Lesson 2", "Content 2", 2);
        UpdateLessonReq lessonReq3 = new UpdateLessonReq("Lesson 3", "Content 3", 0);
        List<UpdateLessonReq> lessons = List.of(lessonReq1, lessonReq2, lessonReq3);

        UpdateCourseReq req = new UpdateCourseReq("Updated Title", "Updated Description", 100.0, 1, lessons);

        Instructor instructor = new Instructor();
        instructor.setId(userId);

        Category category = new Category();
        category.setId(1);

        Course course = new Course();
        course.setId(courseId);
        course.setInstructor(instructor);
        course.setStatus(Status.CREATED);
        course.setVersion(1);

        Lesson existingLesson1 = new Lesson();
        existingLesson1.setId(1);

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(courseRepository.findByIdAndInstructorId(courseId,
                userId)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(lessonRepository.findById(1)).thenReturn(Optional.of(existingLesson1));
        when(lessonRepository.findById(anyInt())).thenReturn(Optional.empty());
        Course updatedCourse = courseService.update(courseId, req);

        assertThat(updatedCourse.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedCourse.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedCourse.getPrice()).isEqualTo(100.0);
        assertThat(updatedCourse.getCategory()).isEqualTo(category);
    }

    @Test
    public void testUpdateCourse_Rejected() {
        int courseId = 1;
        int userId = 1;

        UpdateLessonReq lessonReq1 = new UpdateLessonReq("Lesson 1", "Content 1", 1);
        UpdateLessonReq lessonReq2 = new UpdateLessonReq("Lesson 2", "Content 2", 2);
        UpdateLessonReq lessonReq3 = new UpdateLessonReq("Lesson 3", "Content 3", 0);
        List<UpdateLessonReq> lessons = List.of(lessonReq1, lessonReq2, lessonReq3);

        UpdateCourseReq req = new UpdateCourseReq("Updated Title", "Updated Description", 100.0, 1, lessons);

        Instructor instructor = new Instructor();
        instructor.setId(userId);

        Category category = new Category();
        category.setId(1);

        Course course = new Course();
        course.setId(courseId);
        course.setInstructor(instructor);
        course.setStatus(Status.REJECTED);
        course.setVersion(1);

        Lesson existingLesson1 = new Lesson();
        existingLesson1.setId(1);

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(courseRepository.findByIdAndInstructorId(courseId,
                userId)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(lessonRepository.findById(1)).thenReturn(Optional.of(existingLesson1));
        when(lessonRepository.findById(anyInt())).thenReturn(Optional.empty());
        Course updatedCourse = courseService.update(courseId, req);

        assertThat(updatedCourse.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedCourse.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedCourse.getPrice()).isEqualTo(100.0);
        assertThat(updatedCourse.getCategory()).isEqualTo(category);
    }

    @Test
    public void testUpdateCourse_Success_StatusApproved() {
        int courseId = 1;
        int userId = 1;

        UpdateLessonReq lessonReq1 = new UpdateLessonReq("Lesson 1", "Content 1", 1);
        UpdateLessonReq lessonReq2 = new UpdateLessonReq("Lesson 2", "Content 2", 2);
        UpdateLessonReq lessonReq3 = new UpdateLessonReq("Lesson 3", "Content 3", 0);
        List<UpdateLessonReq> lessons = List.of(lessonReq1, lessonReq2, lessonReq3);

        UpdateCourseReq req = new UpdateCourseReq("Updated Title", "Updated Description", 100.0, 1, lessons);

        Instructor instructor = new Instructor();
        instructor.setId(userId);

        Category category = new Category();
        category.setId(1);

        Course course = new Course();
        course.setId(courseId);
        course.setInstructor(instructor);
        course.setStatus(Status.APPROVED);
        course.setVersion(1);

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(courseRepository.findByIdAndInstructorId(courseId, userId)).thenReturn(Optional.of(course));

        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Lesson existingLesson1 = new Lesson();
        existingLesson1.setId(1);
        Lesson existingLesson2 = new Lesson();
        existingLesson2.setId(2);

        when(lessonRepository.findById(1)).thenReturn(Optional.of(existingLesson1));
        when(lessonRepository.findById(2)).thenReturn(Optional.of(existingLesson2));
        when(lessonRepository.findById(0)).thenReturn(null);
        Course updatedCourse = courseService.update(courseId, req);

        assertThat(updatedCourse.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedCourse.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedCourse.getPrice()).isEqualTo(100.0);
        assertThat(updatedCourse.getCategory()).isEqualTo(category);

        verify(lessonRepository, times(3)).save(any(Lesson.class));

        verify(getAuthUserInfo, times(1)).getAuthUserId();
        verify(instructorRepository, times(1)).findById(userId);
        verify(categoryRepository, times(1)).findById(1);
        verify(courseRepository, times(1)).findByIdAndInstructorId(courseId, userId);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    public void testUpdateCourse_Success_StatusApproved_HasAnotherVersion() {
        int courseId = 1;
        int userId = 1;

        UpdateLessonReq lessonReq1 = new UpdateLessonReq("Lesson 1", "Content 1", 1);
        UpdateLessonReq lessonReq2 = new UpdateLessonReq("Lesson 2", "Content 2", 2);
        UpdateLessonReq lessonReq3 = new UpdateLessonReq("Lesson 3", "Content 3", 0);
        List<UpdateLessonReq> lessons = List.of(lessonReq1, lessonReq2, lessonReq3);

        UpdateCourseReq req = new UpdateCourseReq("Updated Title", "Updated Description", 100.0, 1, lessons);

        Instructor instructor = new Instructor();
        instructor.setId(userId);

        Category category = new Category();
        category.setId(1);

        Course course = new Course();
        course.setId(courseId);
        course.setInstructor(instructor);
        course.setStatus(Status.APPROVED);
        course.setVersion(1);
        course.setCodeCourse("CODE123");

        Course anotherVersionCourse = new Course();
        anotherVersionCourse.setId(2);
        anotherVersionCourse.setInstructor(instructor);
        anotherVersionCourse.setStatus(Status.CREATED);
        anotherVersionCourse.setVersion(2);
        anotherVersionCourse.setCodeCourse("CODE123");

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(courseRepository.findByIdAndInstructorId(courseId, userId)).thenReturn(Optional.of(course));
        when(courseRepository.findCourseOtherVersion("CODE123")).thenReturn(List.of(anotherVersionCourse));

        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Lesson existingLesson1 = new Lesson();
        existingLesson1.setId(1);
        Lesson existingLesson2 = new Lesson();
        existingLesson2.setId(2);

        when(lessonRepository.findById(1)).thenReturn(Optional.of(existingLesson1));
        when(lessonRepository.findById(2)).thenReturn(Optional.of(existingLesson2));
        when(lessonRepository.findById(0)).thenReturn(Optional.empty());

        Course updatedCourse = courseService.update(courseId, req);

        assertThat(updatedCourse.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedCourse.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedCourse.getPrice()).isEqualTo(100.0);
        assertThat(updatedCourse.getCategory()).isEqualTo(category);

        verify(lessonRepository, times(3)).save(any(Lesson.class));

        verify(getAuthUserInfo, times(1)).getAuthUserId();
        verify(instructorRepository, times(1)).findById(userId);
        verify(categoryRepository, times(1)).findById(1);
        verify(courseRepository, times(1)).findByIdAndInstructorId(courseId, userId);
        verify(courseRepository, times(1)).save(any(Course.class));

        verify(lessonRepository, times(1))
                .save(argThat(lesson -> lesson.getOld_id() == 1 && lesson.getCourse().equals(updatedCourse)));
        verify(lessonRepository, times(1))
                .save(argThat(lesson -> lesson.getOld_id() == 2 && lesson.getCourse().equals(updatedCourse)));
        verify(lessonRepository, times(1))
                .save(argThat(lesson -> lesson.getOld_id() == 0 && lesson.getCourse().equals(updatedCourse)));
    }

    @Test
    public void testUpdateCourse_Success_StatusApproved_HasAnotherVersionPending() {
        int courseId = 1;
        int userId = 1;

        UpdateLessonReq lessonReq1 = new UpdateLessonReq("Lesson 1", "Content 1", 1);
        UpdateLessonReq lessonReq2 = new UpdateLessonReq("Lesson 2", "Content 2", 2);
        UpdateLessonReq lessonReq3 = new UpdateLessonReq("Lesson 3", "Content 3", 0);
        List<UpdateLessonReq> lessons = List.of(lessonReq1, lessonReq2, lessonReq3);

        UpdateCourseReq req = new UpdateCourseReq("Updated Title", "Updated Description", 100.0, 1, lessons);

        Instructor instructor = new Instructor();
        instructor.setId(userId);

        Category category = new Category();
        category.setId(1);

        Course course = new Course();
        course.setId(courseId);
        course.setInstructor(instructor);
        course.setStatus(Status.APPROVED);
        course.setVersion(1);
        course.setCodeCourse("CODE123");

        Course anotherVersionCourse = new Course();
        anotherVersionCourse.setId(2);
        anotherVersionCourse.setInstructor(instructor);
        anotherVersionCourse.setStatus(Status.PENDING);
        anotherVersionCourse.setVersion(2);
        anotherVersionCourse.setCodeCourse("CODE123");

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(courseRepository.findByIdAndInstructorId(courseId, userId)).thenReturn(Optional.of(course));
        when(courseRepository.findCourseOtherVersion("CODE123")).thenReturn(List.of(anotherVersionCourse));

        AppException thrownException = assertThrows(AppException.class, () -> {
            courseService.update(courseId, req);
        });

        assertThat(thrownException.getErrorCode()).isEqualTo(ErrorCode.COURSE_UPDATE_DENIED);

        verify(getAuthUserInfo, times(1)).getAuthUserId();
        verify(instructorRepository, times(1)).findById(userId);
        verify(categoryRepository, times(1)).findById(1);
        verify(courseRepository, times(1)).findByIdAndInstructorId(courseId, userId);
        verify(courseRepository, times(1)).findCourseOtherVersion("CODE123");
        verifyNoMoreInteractions(courseRepository, lessonRepository, instructorRepository, categoryRepository);
    }

    @Test
    public void testUpdateCourse_CoursePending() {
        int courseId = 1;
        int userId = 1;

        UpdateCourseReq req = new UpdateCourseReq();
        Instructor instructor = new Instructor();
        instructor.setId(userId);

        Course course = new Course();
        course.setId(courseId);
        course.setInstructor(instructor);
        course.setStatus(Status.PENDING);

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(courseRepository.findByIdAndInstructorId(courseId,
                userId)).thenReturn(Optional.of(course));

        assertThrows(AppException.class, () -> {
            courseService.update(courseId, req);
        });
    }

    @Test
    public void testUpdate_CategoryNotFound() {
        int userId = 1;
        int categoryId = 1;
        int courseId = 1;
        UpdateCourseReq req = new UpdateCourseReq();
        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.update(courseId, req))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }

    @Test
    public void testUpdateCourse_CourseNotFound() {
        int userId = 1;
        int categoryId = 1;
        int courseId = 1;
        UpdateCourseReq req = new UpdateCourseReq();
        req.setCategoryId(categoryId);

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(courseRepository.findByIdAndInstructorId(courseId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.update(courseId, req))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.COURSE_NOT_FOUND.getMessage());
    }

    @Test
    public void testUpdateCourse_InstructorNotFound() {
        int userId = 1;
        int courseId = 1;
        UpdateCourseReq req = new UpdateCourseReq();
        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.update(courseId, req))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INSTRUCTOR_NOT_FOUND.getMessage());
    }

    @Test
    public void testUpdateCourse_UpdateDenied_PendingStatus() {
        int userId = 1;
        int courseId = 1;
        int categoryId = 1;
        UpdateCourseReq req = new UpdateCourseReq();
        req.setCategoryId(1);
        req.setTitle("New Title");
        req.setDescription("New Description");
        req.setPrice(200.0);

        Instructor instructor = new Instructor();
        instructor.setId(userId);

        Course course = new Course();
        course.setId(courseId);
        course.setStatus(Status.PENDING);
        course.setInstructor(instructor);

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(courseRepository.findByIdAndInstructorId(courseId, userId)).thenReturn(Optional.of(course));

        AppException thrownException = null;
        try {
            courseService.update(courseId, req);
        } catch (AppException e) {
            thrownException = e;
        }

        assertThat(thrownException).isNotNull();
        assertThat(thrownException.getErrorCode()).isEqualTo(ErrorCode.COURSE_UPDATE_DENIED);
    }

    @Test
    public void testDelete_Success() {
        int courseId = 1;
        String courseCode = "COURSE_CODE";

        Course course = new Course();
        course.setId(courseId);
        course.setCodeCourse(courseCode);

        Course otherVersionCourse = new Course();
        otherVersionCourse.setId(2);
        otherVersionCourse.setCodeCourse(courseCode);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.findCourseOtherVersion(courseCode)).thenReturn(List.of(otherVersionCourse));

        courseService.delete(courseId);

        verify(courseRepository, times(1)).deleteById(otherVersionCourse.getId());
        verify(courseRepository, times(1)).deleteById(courseId);
    }

    @Test
    public void testDelete_CourseNotFound() {
        int courseId = 1;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        courseService.delete(courseId);

        verify(courseRepository, never()).deleteById(anyInt());
    }

    @Test
    public void testDelete_NoOtherVersions() {
        int courseId = 1;
        String courseCode = "COURSE_CODE";

        Course course = new Course();
        course.setId(courseId);
        course.setCodeCourse(courseCode);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.findCourseOtherVersion(courseCode)).thenReturn(List.of());

        courseService.delete(courseId);

        verify(courseRepository, times(1)).deleteById(courseId);
    }

    @Test
    public void testDelete_WithOtherVersions() {
        int courseId = 1;
        String courseCode = "COURSE_CODE";

        Course course = new Course();
        course.setId(courseId);
        course.setCodeCourse(courseCode);

        Course otherVersion1 = new Course();
        otherVersion1.setId(2);
        Course otherVersion2 = new Course();
        otherVersion2.setId(3);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.findCourseOtherVersion(courseCode)).thenReturn(List.of(otherVersion1, otherVersion2));

        courseService.delete(courseId);

        verify(courseRepository, times(1)).deleteById(courseId);
        verify(courseRepository, times(1)).deleteById(2);
        verify(courseRepository, times(1)).deleteById(3);
    }

    @Test
    public void testReviewCourse_SuccessOriginalCourse() {
        int courseId = 1;
        ReviewCourseReq req = new ReviewCourseReq(courseId, Status.APPROVED);

        Course course = new Course();
        course.setId(courseId);
        course.setStatus(Status.PENDING);
        course.setVersion(1);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        courseService.reviewCourse(req);

        assertThat(course.getStatus()).isEqualTo(Status.APPROVED);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testReviewCourse_SuccessUpdatedCourse() {
        int courseId = 1;
        String codeCourse = "code123";
        ReviewCourseReq req = new ReviewCourseReq(courseId, Status.APPROVED);

        Course currCourse = new Course();
        currCourse.setId(courseId);
        currCourse.setStatus(Status.PENDING);
        currCourse.setVersion(2);
        currCourse.setCodeCourse(codeCourse);
        currCourse.setTitle("New Title");
        currCourse.setDescription("New Description");
        currCourse.setPrice(200.0);
        currCourse.setCategory(new Category());
        currCourse.setLessons(new HashSet<>());

        Course originalCourse = new Course();
        originalCourse.setVersion(1);
        originalCourse.setCodeCourse(codeCourse);
        originalCourse.setLessons(new HashSet<>());

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(currCourse));
        when(courseRepository.findCourseOriginalVersion(codeCourse)).thenReturn(originalCourse);

        courseService.reviewCourse(req);

        assertThat(originalCourse.getTitle()).isEqualTo(currCourse.getTitle());
        assertThat(originalCourse.getDescription()).isEqualTo(currCourse.getDescription());
        assertThat(originalCourse.getPrice()).isEqualTo(currCourse.getPrice());
        assertThat(originalCourse.getCategory()).isEqualTo(currCourse.getCategory());
        verify(courseRepository, times(1)).delete(currCourse);
        verify(courseRepository, times(1)).save(originalCourse);
    }

    @Test
    public void testReviewCourse_CourseNotFound() {
        int courseId = 1;
        ReviewCourseReq req = new ReviewCourseReq(courseId, Status.APPROVED);

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.reviewCourse(req))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.COURSE_NOT_FOUND.getMessage());
    }

    @Test
    public void testReviewCourse_CourseNotPending() {
        int courseId = 1;
        ReviewCourseReq req = new ReviewCourseReq(courseId, Status.APPROVED);

        Course course = new Course();
        course.setId(courseId);
        course.setStatus(Status.APPROVED);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.reviewCourse(req))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.COURSE_UPDATE_DENIED.getMessage());
    }

    @Test
    public void testReviewCourse_SuccessRejectedCourse() {
        int courseId = 1;
        ReviewCourseReq req = new ReviewCourseReq(courseId, Status.REJECTED);

        Course course = new Course();
        course.setId(courseId);
        course.setStatus(Status.PENDING);
        course.setVersion(2);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        courseService.reviewCourse(req);

        assertThat(course.getStatus()).isEqualTo(Status.REJECTED);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testReviewCourse_SuccessLessonUpdate() {
        int courseId = 1;
        String codeCourse = "code123";
        ReviewCourseReq req = new ReviewCourseReq(courseId, Status.APPROVED);

        Course currCourse = new Course();
        currCourse.setId(courseId);
        currCourse.setStatus(Status.PENDING);
        currCourse.setVersion(2);
        currCourse.setCodeCourse(codeCourse);
        currCourse.setTitle("New Title");
        currCourse.setDescription("New Description");
        currCourse.setPrice(200.0);
        currCourse.setCategory(new Category());

        Lesson lesson1 = new Lesson();
        lesson1.setOld_id(1);
        lesson1.setTitle("Lesson 1");
        lesson1.setContent("Content 1");

        Lesson lesson2 = new Lesson();
        lesson2.setOld_id(0);
        lesson2.setTitle("Lesson 2");
        lesson2.setContent("Content 2");

        currCourse.setLessons(new HashSet<>(Set.of(lesson1, lesson2)));

        Course originalCourse = new Course();
        originalCourse.setVersion(1);
        originalCourse.setCodeCourse(codeCourse);
        originalCourse.setLessons(new HashSet<>());

        Lesson oldLesson = new Lesson();
        oldLesson.setId(1);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(currCourse));
        when(courseRepository.findCourseOriginalVersion(codeCourse)).thenReturn(originalCourse);
        when(lessonRepository.findById(1)).thenReturn(Optional.of(oldLesson));

        courseService.reviewCourse(req);

        verify(lessonRepository, times(2)).save(any(Lesson.class));
        verify(courseRepository, times(1)).delete(currCourse);
        verify(courseRepository, times(1)).save(originalCourse);

        assertThat(oldLesson.getTitle()).isEqualTo("Lesson 1");
        assertThat(oldLesson.getContent()).isEqualTo("Content 1");
        assertThat(oldLesson.getOld_id()).isEqualTo(0);
    }

    @Test
    public void testGetAllForStudent() {
        CourseParamReq req = new CourseParamReq();
        req.setPage(2);
        req.setPageSize(10);

        PageRequest expectedPageRequest = PageRequest.of(1, 10, Sort.by("title").descending());
        Page<CourseWithRatingRes> expectedPage = new PageImpl<>(Collections.emptyList());

        when(courseRepository.findByKeywordOnlyActivate(eq(""), eq(expectedPageRequest)))
                .thenReturn(expectedPage);

        Page<CourseWithRatingRes> result = courseService.getAllForStudent(req);

        verify(courseRepository).findByKeywordOnlyActivate("", expectedPageRequest);
        assertThat(result).isEqualTo(expectedPage);

        req.setPage(0);

        expectedPageRequest = PageRequest.of(0, 10, Sort.by("title").descending());

        when(courseRepository.findByKeywordOnlyActivate(eq(""), eq(expectedPageRequest)))
                .thenReturn(expectedPage);

        result = courseService.getAllForStudent(req);

        verify(courseRepository, times(1)).findByKeywordOnlyActivate("", expectedPageRequest);
        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    public void testGetAllForStudentWithNoCategory() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("title").descending());
        Page<CourseWithRatingRes> expectedPage = new PageImpl<>(Collections.emptyList());

        when(courseRepository.findByKeywordOnlyActivate(eq("Java"), eq(pageRequest)))
                .thenReturn(expectedPage);

        Page<CourseWithRatingRes> result = courseService.getAllForStudent(req);

        verify(courseRepository).findByKeywordOnlyActivate("Java", pageRequest);
        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    public void testGetAllForStudentWithCategory() {
        req.setCategoryId(1);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("title").descending());
        Page<CourseWithRatingRes> expectedPage = new PageImpl<>(Collections.emptyList());

        when(courseRepository.findByKeywordWithCategoryOnlyActivate(eq("Java"), eq(1), eq(pageRequest)))
                .thenReturn(expectedPage);

        Page<CourseWithRatingRes> result = courseService.getAllForStudent(req);

        verify(courseRepository).findByKeywordWithCategoryOnlyActivate("Java", 1, pageRequest);
        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    public void testGetAllForStudentWithSorting() {
        req.setSortBy(new String[] { "title" });
        req.setSortDirection(new String[] { "asc" });

        Sort sort = Sort.by("title").ascending();
        PageRequest pageRequest = PageRequest.of(0, 10, sort);
        Page<CourseWithRatingRes> expectedPage = new PageImpl<>(Collections.emptyList());

        when(courseRepository.findByKeywordOnlyActivate(eq("Java"), eq(pageRequest)))
                .thenReturn(expectedPage);

        Page<CourseWithRatingRes> result = courseService.getAllForStudent(req);

        verify(courseRepository).findByKeywordOnlyActivate("Java", pageRequest);
        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    public void testGetAllForInstructor() {
        InstructorCourseParamReq req = new InstructorCourseParamReq();
        req.setPage(2);
        req.setPageSize(10);
        req.setKeyword("keyword");

        int userId = 1;
        Instructor instructor = new Instructor();
        instructor.setId(userId);

        PageRequest expectedPageRequest = PageRequest.of(1, 10,
                Sort.by("title").descending().and(Sort.by("updated_at").descending()));
        Page<Course> expectedPage = new PageImpl<>(Collections.emptyList());

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(courseRepository.findCourseByInstructor(eq("keyword"), eq(userId), eq(expectedPageRequest)))
                .thenReturn(expectedPage);

        Page<Course> result = courseService.getAllForInstructor(req);

        verify(getAuthUserInfo).getAuthUserId();
        verify(instructorRepository).findById(userId);
        verify(courseRepository).findCourseByInstructor("keyword", userId, expectedPageRequest);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedPage);

        req.setPage(0);
        expectedPageRequest = PageRequest.of(0, 10,
                Sort.by("title").descending().and(Sort.by("updated_at").descending()));

        when(courseRepository.findCourseByInstructor(eq("keyword"), eq(userId), eq(expectedPageRequest)))
                .thenReturn(expectedPage);

        result = courseService.getAllForInstructor(req);

        verify(courseRepository, times(1)).findCourseByInstructor("keyword", userId, expectedPageRequest);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    public void testGetAllForInstructor_InstructorNotFound() {
        InstructorCourseParamReq req = new InstructorCourseParamReq();
        req.setPage(1);
        req.setPageSize(10);

        int userId = 1;

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> courseService.getAllForInstructor(req));

        verify(getAuthUserInfo).getAuthUserId();
        verify(instructorRepository).findById(userId);
        assertThat(exception.getErrorCode().getMessage()).isEqualTo(ErrorCode.INSTRUCTOR_NOT_FOUND.getMessage());
    }

    @Test
    public void testGetAllForInstructor_Success() {
        int userId = 1;
        String keyword = "test";
        int page = 1;
        int pageSize = 10;

        InstructorCourseParamReq req = new InstructorCourseParamReq();
        req.setPage(page);
        req.setPageSize(pageSize);
        req.setKeyword(keyword);

        Instructor instructor = new Instructor();
        instructor.setId(userId);

        List<Course> courses = List.of(new Course(), new Course());
        Page<Course> coursePage = new PageImpl<>(courses);

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(courseRepository.findCourseByInstructor(anyString(), anyInt(), any(PageRequest.class)))
                .thenReturn(coursePage);

        Page<Course> result = courseService.getAllForInstructor(req);

        assertThat(result.getContent()).isEqualTo(courses);
        assertThat(result.getTotalElements()).isEqualTo(courses.size());
    }

    @Test
    public void testCreate_InstructorNotFound() {
        int userId = 1;
        CreateCourseReq req = new CreateCourseReq();
        req.setCategoryId(1);
        req.setTitle("Course Title");
        req.setDescription("Course Description");
        req.setPrice(100.0);
        req.setLessons(List.of(new CreateLessonReq("Lesson 1", "Content 1")));

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> {
            courseService.create(req);
        });
    }

    @Test
    public void testCombineIntoSortDescending() {
        String[] sortBy = { "title", "updated_at" };
        String[] sortDirection = { "desc", "desc" };

        Sort result = courseService.combineIntoSort(sortBy, sortDirection);

        List<Sort.Order> orders = result.toList();
        assertThat(orders).hasSize(2);
        assertThat(orders.get(0).getProperty()).isEqualTo("title");
        assertThat(orders.get(0).getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(orders.get(1).getProperty()).isEqualTo("updated_at");
        assertThat(orders.get(1).getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    public void testCombineIntoSortWithUnequalLengthArrays() {
        String[] sortBy = { "title", "updated_at" };
        String[] sortDirection = { "asc" };

        Sort result = courseService.combineIntoSort(sortBy, sortDirection);

        List<Sort.Order> orders = result.toList();
        assertThat(orders).hasSize(2);
        assertThat(orders.get(0).getProperty()).isEqualTo("title");
        assertThat(orders.get(0).getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(orders.get(1).getProperty()).isEqualTo("updated_at");
        assertThat(orders.get(1).getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    public void testCreate_CategoryNotFound() {
        int userId = 1;
        CreateCourseReq req = new CreateCourseReq();
        req.setCategoryId(1);
        req.setTitle("Course Title");
        req.setDescription("Course Description");
        req.setPrice(100.0);
        req.setLessons(List.of(new CreateLessonReq("Lesson 1", "Content 1")));

        Instructor instructor = new Instructor();
        instructor.setId(userId);

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(categoryRepository.findById(req.getCategoryId())).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> {
            courseService.create(req);
        });
    }

    @Test
    public void testCreate_Success() {
        int userId = 1;
        int categoryId = 1;
        String title = "Course Title";
        String description = "Course Description";
        double price = 100.0;
        List<CreateLessonReq> lessons = List.of(
                new CreateLessonReq("Lesson 1", "Content 1"),
                new CreateLessonReq("Lesson 2", "Content 2"));

        CreateCourseReq req = new CreateCourseReq();
        req.setCategoryId(categoryId);
        req.setTitle(title);
        req.setDescription(description);
        req.setPrice(price);
        req.setLessons(lessons);

        Instructor instructor = new Instructor();
        instructor.setId(userId);

        Category category = new Category();
        category.setId(categoryId);

        Course newCourse = new Course();
        newCourse.setId(1);
        newCourse.setTitle(title);
        newCourse.setDescription(description);
        newCourse.setPrice(price);
        newCourse.setCategory(category);
        newCourse.setStatus(Status.CREATED);
        newCourse.setInstructor(instructor);
        newCourse.setVersion(1);
        newCourse.setCodeCourse(UUID.randomUUID().toString());

        Set<Lesson> lessonSet = new HashSet<>();
        for (CreateLessonReq dto : lessons) {
            Lesson lesson = new Lesson();
            lesson.setTitle(dto.getTitle());
            lesson.setContent(dto.getContent());
            lesson.setCreated_at(LocalDateTime.now());
            lesson.setUpdated_at(LocalDateTime.now());
            lesson.setCourse(newCourse);
            lessonSet.add(lesson);
            when(lessonRepository.save(lesson)).thenReturn(lesson);
        }
        newCourse.setLessons(lessonSet);

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(courseRepository.save(any(Course.class))).thenReturn(newCourse);

        Course result = courseService.create(req);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(newCourse.getId());
        assertThat(result.getTitle()).isEqualTo(newCourse.getTitle());
        assertThat(result.getDescription()).isEqualTo(newCourse.getDescription());
        assertThat(result.getPrice()).isEqualTo(newCourse.getPrice());
        assertThat(result.getCategory()).isEqualTo(newCourse.getCategory());
        assertThat(result.getStatus()).isEqualTo(newCourse.getStatus());
        assertThat(result.getInstructor()).isEqualTo(newCourse.getInstructor());
        assertThat(result.getVersion()).isEqualTo(newCourse.getVersion());
        assertThat(result.getCodeCourse()).isEqualTo(newCourse.getCodeCourse());

        Set<Lesson> responseLessons = result.getLessons();
        assertThat(responseLessons).hasSize(lessons.size());

        for (CreateLessonReq expectedLesson : lessons) {
            boolean matchFound = responseLessons.stream()
                    .anyMatch(lesson -> lesson.getContent().equals(expectedLesson.getContent()) &&
                            lesson.getTitle().equals(expectedLesson.getTitle()));
            assertThat(matchFound).isTrue();
        }
    }

    @Test
    public void testSubmitCourse_Success() {
        int courseId = 1;
        SubmitCourseReq req = new SubmitCourseReq(courseId);

        Course course = new Course();
        course.setId(courseId);
        course.setStatus(Status.CREATED);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        courseService.submitCourse(req);

        assertThat(course.getStatus()).isEqualTo(Status.PENDING);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testSubmitCourse_CourseStatusApproved() {
        SubmitCourseReq req = new SubmitCourseReq();
        req.setId(1);

        Course course = new Course();
        course.setId(1);
        course.setStatus(Status.APPROVED);

        when(courseRepository.findById(req.getId())).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.submitCourse(req))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.SUBMIT_COURSE_FAIL.getMessage());

        verify(courseRepository, times(1)).findById(req.getId());
        verify(courseRepository, times(0)).save(any(Course.class));
    }

    @Test
    public void testSubmitCourse_CourseNotFound() {
        int courseId = 1;
        SubmitCourseReq req = new SubmitCourseReq(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.submitCourse(req))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.COURSE_NOT_FOUND.getMessage());
    }

    @Test
    public void testGetAllForAdmin() {
        AdminCourseParamReq req = new AdminCourseParamReq();
        req.setPage(2);
        req.setPageSize(10);
        req.setTitle("Test Title");
        req.setStatus(Status.CREATED);

        PageRequest expectedPageRequest = PageRequest.of(1, 10,
                Sort.by("title").descending().and(Sort.by("updated_at").descending()));
        Page<Course> expectedPage = new PageImpl<>(Collections.emptyList());

        when(courseRepository.findByTitleAndStatus(eq(req.getTitle()), eq(req.getStatus()), eq(expectedPageRequest)))
                .thenReturn(expectedPage);

        Page<Course> result = courseService.getAllForAdmin(req);

        verify(courseRepository).findByTitleAndStatus(req.getTitle(), req.getStatus(), expectedPageRequest);
        assertThat(result).isEqualTo(expectedPage);

        req.setPage(0);

        expectedPageRequest = PageRequest.of(0, 10,
                Sort.by("title").descending().and(Sort.by("updated_at").descending()));

        when(courseRepository.findByTitleAndStatus(eq(req.getTitle()), eq(req.getStatus()), eq(expectedPageRequest)))
                .thenReturn(expectedPage);

        result = courseService.getAllForAdmin(req);

        verify(courseRepository, times(1)).findByTitleAndStatus(req.getTitle(), req.getStatus(), expectedPageRequest);
        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    public void testGetOneById_Success() {
        int courseId = 1;
        Course course = new Course();
        course.setId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        Course result = courseService.getOneById(courseId);

        assertThat(result).isEqualTo(course);
    }

    @Test
    public void testGetOneById_CourseNotFound() {
        int courseId = 1;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getOneById(courseId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.COURSE_NOT_FOUND.getMessage());
    }
}
