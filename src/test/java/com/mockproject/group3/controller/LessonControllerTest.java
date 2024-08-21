package com.mockproject.group3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockproject.group3.dto.LessonDTO;
import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Lesson;
import com.mockproject.group3.service.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LessonControllerTest {

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private LessonController lessonController;

    @InjectMocks
    private EnrollmentController enrollmentController;

    private MockMvc mockMvc;

    private Lesson lesson;
    private LessonDTO lessonDTO;
    private PaginationParamReq paginationParamReq;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(enrollmentController, lessonController).build();

        lesson = new Lesson();
        lesson.setId(1);
        lesson.setTitle("Lesson Title");
        lesson.setContent("Lesson Content");

        lessonDTO = new LessonDTO();
        lessonDTO.setTitle("Lesson Title");
        lessonDTO.setContent("Lesson Content");

        paginationParamReq = new PaginationParamReq();
        paginationParamReq.setPage(1);
        paginationParamReq.setPageSize(10);
    }

    @Test
    void testCreateLesson() throws Exception {
        when(lessonService.createLesson(any(LessonDTO.class))).thenReturn(lesson);

        mockMvc.perform(post("/api/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(lessonDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Lesson Title"))
                .andExpect(jsonPath("$.content").value("Lesson Content"));
    }

    @Test
    void testGetAllLessons() throws Exception {
        Page<Lesson> lessonPage = new PageImpl<>(List.of(lesson), PageRequest.of(0, 10), 1);
        when(lessonService.getAllLessons(any(PaginationParamReq.class))).thenReturn(lessonPage);

        mockMvc.perform(get("/api/lessons")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("Get list lesson successfully"))
                .andExpect(jsonPath("$.payload[0].id").value(1))
                .andExpect(jsonPath("$.payload[0].title").value("Lesson Title"))
                .andExpect(jsonPath("$.payload[0].content").value("Lesson Content"))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalPage").value(1))
                .andExpect(jsonPath("$.totalItem").value(1));
    }

    @Test
    void testGetLessonById() throws Exception {
        when(lessonService.getLessonById(anyInt())).thenReturn(Optional.of(lesson));

        mockMvc.perform(get("/api/lessons/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Lesson Title"))
                .andExpect(jsonPath("$.content").value("Lesson Content"));
    }

    @Test
    void testGetLessonsByCourseId() throws Exception {
        // Tạo đối tượng Course
        Course course = new Course();
        course.setId(1);

        // Tạo đối tượng Lesson
        Lesson lesson1 = new Lesson();
        lesson1.setId(1);
        lesson1.setTitle("Lesson 1");
        lesson1.setContent("Content 1");
        lesson1.setCourse(course);

        Lesson lesson2 = new Lesson();
        lesson2.setId(2);
        lesson2.setTitle("Lesson 2");
        lesson2.setContent("Content 2");
        lesson2.setCourse(course);

        List<Lesson> lessons = Arrays.asList(lesson1, lesson2);

        // Mô phỏng hành vi của service để trả về danh sách lesson
        when(lessonService.getLessonByCourseId(1)).thenReturn(lessons);

        // Thực hiện yêu cầu GET và xác minh kết quả
        mockMvc.perform(get("/api/lessons/course/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Lesson 1"))
                .andExpect(jsonPath("$[0].content").value("Content 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Lesson 2"))
                .andExpect(jsonPath("$[1].content").value("Content 2"));
    }

    @Test
    void testUpdateLesson() throws Exception {
        when(lessonService.updateLesson(anyInt(), any(LessonDTO.class))).thenReturn(lesson);

        mockMvc.perform(put("/api/lessons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(lessonDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Lesson Title"))
                .andExpect(jsonPath("$.content").value("Lesson Content"));
    }

    @Test
    void testDeleteLesson() throws Exception {
        // Mock the service method to do nothing (void method)
        doNothing().when(lessonService).deleteLesson(1);

        // Perform the delete request
        mockMvc.perform(delete("/api/lessons/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify that the service method was called with the correct ID
        verify(lessonService, times(1)).deleteLesson(1);
    }
}
