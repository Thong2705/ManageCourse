package com.mockproject.group3.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import com.mockproject.group3.dto.CategoryDTO;
import com.mockproject.group3.dto.request.course.AdminCourseParamReq;
import com.mockproject.group3.dto.request.course.CourseParamReq;
import com.mockproject.group3.dto.request.course.CreateCourseReq;
import com.mockproject.group3.dto.request.course.InstructorCourseParamReq;
import com.mockproject.group3.dto.request.course.ReviewCourseReq;
import com.mockproject.group3.dto.request.course.SubmitCourseReq;
import com.mockproject.group3.dto.request.course.UpdateCourseReq;
import com.mockproject.group3.dto.request.lesson.CreateLessonReq;
import com.mockproject.group3.dto.request.lesson.UpdateLessonReq;
import com.mockproject.group3.dto.response.BaseApiPaginationRespone;
import com.mockproject.group3.dto.response.BaseApiResponse;
import com.mockproject.group3.dto.response.course.CourseWithRatingRes;
import com.mockproject.group3.enums.Status;
import com.mockproject.group3.model.Category;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Lesson;
import com.mockproject.group3.service.CategoryService;
import com.mockproject.group3.service.CourseService;

@SpringBootTest
public class CourseControllerTest {

    @Autowired
    private CourseController courseController;

    @MockBean
    private CourseService courseService;

    @MockBean
    private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateCourse() {
        CreateLessonReq lesson1 = new CreateLessonReq("Lesson 1", "Content 1");
        CreateLessonReq lesson2 = new CreateLessonReq("Lesson 2", "Content 2");
        List<CreateLessonReq> lessons = List.of(lesson1, lesson2);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("test");
        categoryDTO.setDescription("test");

        Category category = new Category();
        category.setId(1);
        category.setName("test");
        category.setDescription("test");
        when(categoryService.saveCategory(any(CategoryDTO.class))).thenReturn(category);

        Lesson lesson3 = new Lesson();
        lesson3.setId(1);
        lesson3.setTitle("Lesson 1");
        lesson3.setContent("Content 1");

        Lesson lesson4 = new Lesson();
        lesson4.setId(2);
        lesson4.setTitle("Lesson 2");
        lesson4.setContent("Content 2");

        Set<Lesson> lessonSet = new HashSet<>();
        lessonSet.add(lesson3);
        lessonSet.add(lesson4);

        Course newCourse = new Course();
        newCourse.setId(2);
        newCourse.setTitle("Course Title");
        newCourse.setDescription("Course Description");
        newCourse.setPrice(100.0);
        newCourse.setLessons(lessonSet);
        newCourse.setCategory(category);

        CreateCourseReq req = new CreateCourseReq("Course Title", "Course Description", 100.0, category.getId(),
                lessons);

        when(courseService.create(any(CreateCourseReq.class))).thenReturn(newCourse);

        ResponseEntity<BaseApiResponse<Course>> courseCreated = courseController.createCourse(req);

        UpdateLessonReq lesson5 = new UpdateLessonReq("Lesson 1", "Content 1", 1);
        UpdateLessonReq lesson6 = new UpdateLessonReq("Lesson 2", "Content 2", 2);
        List<UpdateLessonReq> updateLessons = List.of(lesson5, lesson6);

        UpdateCourseReq updateReq = new UpdateCourseReq("Update Course", "Update Description", 200.0, category.getId(),
                updateLessons);

        Lesson lesson7 = new Lesson();
        lesson7.setId(1);
        lesson7.setTitle("Lesson 1");
        lesson7.setContent("Content 1");

        Lesson lesson8 = new Lesson();
        lesson8.setId(2);
        lesson8.setTitle("Lesson 2");
        lesson8.setContent("Content 2");

        Set<Lesson> updateLessonSet = new HashSet<>();
        updateLessonSet.add(lesson7);
        updateLessonSet.add(lesson8);

        Course updateCourse = new Course();
        updateCourse.setId(2);
        updateCourse.setTitle("Update Course");
        updateCourse.setDescription("Update Description");
        updateCourse.setPrice(200.0);
        updateCourse.setLessons(updateLessonSet);
        updateCourse.setCategory(category);

        when(courseService.update(anyInt(), any(UpdateCourseReq.class))).thenReturn(updateCourse);

        ResponseEntity<BaseApiResponse<Course>> response = courseController
                .updateCourse(courseCreated.getBody().getPayload().getId(), updateReq);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        BaseApiResponse<Course> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getCode()).isEqualTo(0);
        assertThat(responseBody.getMessage()).isEqualTo("update course successfully");

        Course responseCourse = responseBody.getPayload();
        assertThat(responseCourse.getId()).isEqualTo(2);
        assertThat(responseCourse.getTitle()).isEqualTo(updateReq.getTitle());
        assertThat(responseCourse.getDescription()).isEqualTo(updateReq.getDescription());
        assertThat(responseCourse.getPrice()).isEqualTo(updateReq.getPrice());

        Set<Lesson> responseLessons = responseCourse.getLessons();
        assertThat(responseLessons).hasSize(updateLessons.size());

        for (UpdateLessonReq expectedLesson : updateLessons) {
            boolean matchFound = responseLessons.stream()
                    .anyMatch(lesson -> lesson.getContent().equals(expectedLesson.getContent()) &&
                            lesson.getTitle().equals(expectedLesson.getTitle()));
            assertThat(matchFound).isTrue();
        }
    }

    @Test
    public void testDeleteCourse() {
        int courseId = 1;
        doNothing().when(courseService).delete(anyInt());

        ResponseEntity<BaseApiResponse<Void>> response = courseController.deleteCourse(courseId);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        BaseApiResponse<Void> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getCode()).isEqualTo(0);
        assertThat(responseBody.getMessage()).isEqualTo("delete course successfully");

        verify(courseService).delete(courseId);
    }

    @Test
    public void testReviewCourse() {
        ReviewCourseReq req = new ReviewCourseReq();
        req.setId(1);
        req.setStatus(Status.APPROVED);

        doNothing().when(courseService).reviewCourse(any(ReviewCourseReq.class));

        ResponseEntity<BaseApiResponse<Void>> response = courseController.reviewCourse(req);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        BaseApiResponse<Void> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getCode()).isEqualTo(0);
        assertThat(responseBody.getMessage()).isEqualTo("Review end. Status of course changed to " + req.getStatus());
    }

    @Test
    public void testGetAllCourseForStudent() {
        List<CourseWithRatingRes> courses = List.of(
                new CourseWithRatingRes(),
                new CourseWithRatingRes());
        Page<CourseWithRatingRes> coursePage = new PageImpl<>(courses, PageRequest.of(0, 10), courses.size());

        when(courseService.getAllForStudent(any(CourseParamReq.class))).thenReturn(coursePage);

        CourseParamReq req = new CourseParamReq();

        ResponseEntity<BaseApiPaginationRespone<List<CourseWithRatingRes>>> response = courseController
                .getAllCourseForStudent(req);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        BaseApiPaginationRespone<List<CourseWithRatingRes>> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getCode()).isEqualTo(0);
        assertThat(responseBody.getMessage()).isEqualTo("Get list course succesfully");
        assertThat(responseBody.getCurrentPage()).isEqualTo(1);
        assertThat(responseBody.getPageSize()).isEqualTo(10);
        assertThat(responseBody.getTotalPage()).isEqualTo(1);
        assertThat(responseBody.getTotalItem()).isEqualTo(courses.size());
    }

    @Test
    public void testGetAllCourseForInstructor() {
        List<Course> courses = List.of(
                new Course(),
                new Course());
        Page<Course> coursePage = new PageImpl<>(courses, PageRequest.of(0, 10), courses.size());

        when(courseService.getAllForInstructor(any(InstructorCourseParamReq.class))).thenReturn(coursePage);

        InstructorCourseParamReq req = new InstructorCourseParamReq();

        ResponseEntity<BaseApiPaginationRespone<List<Course>>> response = courseController
                .getAllCourseForInstructor(req);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        BaseApiPaginationRespone<List<Course>> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getCode()).isEqualTo(0);
        assertThat(responseBody.getMessage()).isEqualTo("Get list course succesfully");
        assertThat(responseBody.getCurrentPage()).isEqualTo(1);
        assertThat(responseBody.getPageSize()).isEqualTo(10);
        assertThat(responseBody.getTotalPage()).isEqualTo(1);
        assertThat(responseBody.getTotalItem()).isEqualTo(courses.size());
    }

    @Test
    public void testSubmitCourse() {
        SubmitCourseReq req = new SubmitCourseReq(1);

        doNothing().when(courseService).submitCourse(req);

        ResponseEntity<BaseApiResponse<Void>> response = courseController.submitCourse(req);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        BaseApiResponse<Void> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getCode()).isEqualTo(0);
        assertThat(responseBody.getMessage()).isEqualTo("submit course successfully");
    }

    @Test
    public void testCreateCourse() {
        CreateLessonReq lesson1 = new CreateLessonReq("Lesson 1", "Content 1");
        CreateLessonReq lesson2 = new CreateLessonReq("Lesson 2", "Content 2");
        List<CreateLessonReq> lessons = List.of(lesson1, lesson2);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("test");
        categoryDTO.setDescription("test");

        Category category = new Category();
        category.setId(1);
        when(categoryService.saveCategory(any(CategoryDTO.class))).thenReturn(category);

        categoryService.saveCategory(categoryDTO);

        Lesson lesson3 = new Lesson();
        lesson3.setTitle("Lesson 1");
        lesson3.setContent("Content 1");

        Lesson lesson4 = new Lesson();
        lesson4.setTitle("Lesson 2");
        lesson4.setContent("Content 2");

        Course newCourse = new Course();
        newCourse.setId(2);
        newCourse.setTitle("Course Title");
        newCourse.setDescription("Course Description");
        newCourse.setPrice(100.0);
        Set<Lesson> lessonSet = new HashSet<>();
        lessonSet.add(lesson3);
        lessonSet.add(lesson4);
        newCourse.setLessons(lessonSet);

        CreateCourseReq req = new CreateCourseReq("Course Title", "Course Description", 100.0, category.getId(),
                lessons);

        when(courseService.create(any(CreateCourseReq.class))).thenReturn(newCourse);

        ResponseEntity<BaseApiResponse<Course>> response = courseController.createCourse(req);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        BaseApiResponse<Course> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getCode()).isEqualTo(0);
        assertThat(responseBody.getMessage()).isEqualTo("create course successfully");
        Course responseCourse = responseBody.getPayload();
        assertThat(responseCourse.getId()).isEqualTo(2);
        assertThat(responseBody.getPayload().getTitle()).isEqualTo(req.getTitle());
        assertThat(responseBody.getPayload().getDescription()).isEqualTo(req.getDescription());
        assertThat(responseBody.getPayload().getPrice()).isEqualTo(req.getPrice());

        Set<Lesson> responseLessons = responseCourse.getLessons();
        assertThat(responseLessons).hasSize(lessons.size());

        for (CreateLessonReq expectedLesson : lessons) {
            boolean matchFound = responseLessons.stream()
                    .anyMatch(lesson -> lesson.getContent().equals(expectedLesson.getContent()) &&
                            lesson.getTitle().equals(expectedLesson.getTitle()));
            assertThat(matchFound).isTrue();
        }
    }

    @Test
    public void testGetAllCourseForAdmin() {
        Course course1 = new Course();
        course1.setId(1);
        course1.setTitle("Course 1");

        Course course2 = new Course();
        course2.setId(2);
        course2.setTitle("Course 2");

        List<Course> courses = List.of(course1, course2);
        Page<Course> page = new PageImpl<>(courses, PageRequest.of(0, 10), courses.size());

        AdminCourseParamReq req = new AdminCourseParamReq();

        when(courseService.getAllForAdmin(any(AdminCourseParamReq.class))).thenReturn(page);

        ResponseEntity<BaseApiPaginationRespone<List<Course>>> response = courseController.getAllCourseForAdmin(req);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        BaseApiPaginationRespone<List<Course>> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getCode()).isEqualTo(0);
        assertThat(responseBody.getMessage()).isEqualTo("Get list course succesfully");
        assertThat(responseBody.getCurrentPage()).isEqualTo(1);
        assertThat(responseBody.getPageSize()).isEqualTo(10);
        assertThat(responseBody.getTotalPage()).isEqualTo(1);
        assertThat(responseBody.getTotalItem()).isEqualTo(courses.size());
    }

    @Test
    public void testGetCourse() {
        Course course = new Course();
        course.setId(1);
        course.setTitle("Course Title");

        when(courseService.getOneById(anyInt())).thenReturn(course);

        ResponseEntity<BaseApiResponse<Course>> response = courseController.getCourse(1);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        BaseApiResponse<Course> responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getCode()).isEqualTo(0);
        assertThat(responseBody.getMessage()).isEqualTo("get course successfully");
        assertThat(responseBody.getPayload()).isEqualTo(course);
    }
}
