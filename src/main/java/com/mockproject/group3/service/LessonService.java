package com.mockproject.group3.service;

import com.mockproject.group3.dto.LessonDTO;
import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Lesson;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.LessonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    public LessonService(LessonRepository lessonRepository, CourseRepository courseRepository) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
    }

    //Create CRUD methods for Lesson
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public Page<Lesson> getAllLessons(PaginationParamReq req) {
        PageRequest pageRequest = PageRequest.of(
                Math.max(req.getPage() - 1, 0),
                req.getPageSize(),
                Sort.by("id").descending()
        );
        return lessonRepository.findAll(pageRequest);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public Optional<Lesson> getLessonById(Integer id) {
        return lessonRepository.findById(id);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public List<Lesson> getLessonByCourseId(Integer courseId) {
        return lessonRepository.findByCourseId(courseId);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public Lesson createLesson(LessonDTO lessonDTO) {
        Lesson lesson = new Lesson();

        lesson.setTitle(lessonDTO.getTitle());
        lesson.setContent(lessonDTO.getContent());

        Course course = courseRepository.findById(lessonDTO.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        lesson.setCourse(course);


        return lessonRepository.save(lesson);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public Lesson updateLesson(Integer id, LessonDTO lessonDTO) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        lesson.setTitle(lessonDTO.getTitle());
        lesson.setContent(lessonDTO.getContent());
        return lessonRepository.save(lesson);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public void deleteLesson(Integer id) {
        lessonRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        lessonRepository.deleteById(id);
    }
}
