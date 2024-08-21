package com.mockproject.group3.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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

@Service
public class CourseService {
    private CourseRepository courseRepository;
    private LessonRepository lessonRepository;
    private CategoryRepository categoryRepository;
    private InstructorRepository instructorRepository;
    private GetAuthUserInfo getAuthUserInfo;

    public CourseService(CourseRepository courseRepository, LessonRepository lessonRepository,
            CategoryRepository categoryRepository,
            InstructorRepository instructorRepository, GetAuthUserInfo getAuthUserInfo) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.categoryRepository = categoryRepository;
        this.instructorRepository = instructorRepository;
        this.getAuthUserInfo = getAuthUserInfo;
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    public Page<CourseWithRatingRes> getAllForStudent(CourseParamReq req) {
        PageRequest pageRequest = PageRequest.of(req.getPage() - 1 >= 0 ? req.getPage() - 1 : 0, req.getPageSize());
        Sort sort = Sort.by("title").descending();

        if (req.getSortBy().length > 0) {
            sort = combineIntoSort(req.getSortBy(), req.getSortDirection());
        }

        if (req.getCategoryId() == 0) {
            return courseRepository.findByKeywordOnlyActivate(req.getKeyword(),
                    pageRequest.withSort(sort));
        }

        return courseRepository.findByKeywordWithCategoryOnlyActivate(req.getKeyword(),
                req.getCategoryId(),
                pageRequest.withSort(sort));
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public Page<Course> getAllForInstructor(InstructorCourseParamReq req) {
        int userId = getAuthUserInfo.getAuthUserId();
        PageRequest pageRequest = PageRequest.of(req.getPage() - 1 >= 0 ? req.getPage() - 1 : 0, req.getPageSize());
        Sort sort = Sort.by("title").descending().and(Sort.by("updated_at").descending());

        instructorRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

        return courseRepository.findCourseByInstructor(req.getKeyword(), userId, pageRequest.withSort(sort));
    }

    public Sort combineIntoSort(String[] sortBy, String[] sortDirection) {
        List<Sort.Order> orders = new ArrayList<>();
        Sort.Direction direction = Sort.Direction.DESC;

        for (int i = 0; i < sortBy.length; i++) {
            direction = Sort.Direction.DESC;
            if (sortDirection.length > i) {
                direction = sortDirection[i].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            }
            orders.add(new Sort.Order(
                    direction,
                    sortBy[i]));
        }
        return Sort.by(orders);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public Course create(CreateCourseReq req) {
        int userId = getAuthUserInfo.getAuthUserId();
        Instructor instructor = instructorRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        Course newCourse = new Course();
        newCourse.setTitle(req.getTitle());
        newCourse.setDescription(req.getDescription());
        newCourse.setPrice(req.getPrice());
        newCourse.setCategory(category);
        newCourse.setStatus(Status.CREATED);
        newCourse.setInstructor(instructor);
        newCourse.setVersion(1);
        newCourse.setCodeCourse(UUID.randomUUID().toString());

        Course courseSaved = courseRepository.save(newCourse);

        for (CreateLessonReq dto : req.getLessons()) {
            Lesson lesson = new Lesson();
            lesson.setCourse(courseSaved);
            lesson.setTitle(dto.getTitle());
            lesson.setContent(dto.getContent());
            lesson.setCreated_at(LocalDateTime.now());
            lesson.setUpdated_at(LocalDateTime.now());
            lessonRepository.save(lesson);
        }

        return courseSaved;
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public Course update(int courseId, UpdateCourseReq req) {
        int userId = getAuthUserInfo.getAuthUserId();
        Instructor instructor = instructorRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        Course course = courseRepository.findByIdAndInstructorId(courseId, instructor.getId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        Course courseSaved;

        if (course.getStatus() == Status.PENDING)
            throw new AppException(ErrorCode.COURSE_UPDATE_DENIED);

        if (course.getStatus() == Status.CREATED || course.getStatus() == Status.REJECTED) {
            course.setTitle(req.getTitle());
            course.setDescription(req.getDescription());
            course.setPrice(req.getPrice());
            course.setCategory(category);
            // course.setStatus(Status.CREATED);
            // course.setInstructor(instructor);

            for (UpdateLessonReq dto : req.getLessons()) {
                Lesson lesson;
                if (dto.getId() == 0)
                    lesson = new Lesson();
                else
                    lesson = lessonRepository.findById(dto.getId())
                            .orElse(null);

                if (lesson == null)
                    continue;

                lesson.setTitle(dto.getTitle());
                lesson.setContent(dto.getContent());
                if (lesson.getCreated_at() == null)
                    lesson.setCreated_at(LocalDateTime.now());
                lesson.setUpdated_at(LocalDateTime.now());
                lesson.setCourse(course);
                lessonRepository.save(lesson);
            }

            courseSaved = courseRepository.save(course);
        } else {
            Course newCourse;
            List<Course> verUpdate = courseRepository.findCourseOtherVersion(course.getCodeCourse());
            boolean hasVerUpdate = false;

            if (verUpdate.size() != 0) {
                newCourse = verUpdate.getFirst();
                if (newCourse.getStatus() == Status.PENDING)
                    throw new AppException(ErrorCode.COURSE_UPDATE_DENIED);
                newCourse.setTitle(req.getTitle());
                newCourse.setDescription(req.getDescription());
                newCourse.setPrice(req.getPrice());
                newCourse.setCategory(category);
                newCourse.setStatus(Status.CREATED);
                hasVerUpdate = true;
            } else {
                newCourse = new Course();
                newCourse.setTitle(req.getTitle());
                newCourse.setDescription(req.getDescription());
                newCourse.setPrice(req.getPrice());
                newCourse.setCategory(category);
                newCourse.setStatus(Status.CREATED);
                newCourse.setInstructor(instructor);
                newCourse.setVersion(course.getVersion() + 1);
                newCourse.setCodeCourse(course.getCodeCourse());
            }

            courseSaved = courseRepository.save(newCourse);

            for (UpdateLessonReq dto : req.getLessons()) {
                Lesson lesson;
                if (hasVerUpdate) {
                    lesson = lessonRepository.findById(dto.getId()).orElse(null);
                    if (lesson == null)
                        lesson = new Lesson();
                } else {
                    // copy lesson tu v1 -> v sau
                    lesson = new Lesson();
                }

                lesson.setTitle(dto.getTitle());
                lesson.setContent(dto.getContent());
                lesson.setCreated_at(LocalDateTime.now());
                lesson.setUpdated_at(LocalDateTime.now());
                if (lesson.getOld_id() == 0)
                    lesson.setOld_id(dto.getId());
                if (lesson.getCourse() == null)
                    lesson.setCourse(courseSaved);
                lessonRepository.save(lesson);
            }

        }

        return courseSaved;
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public void delete(int courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null)
            return;

        List<Course> verUpdate = courseRepository.findCourseOtherVersion(course.getCodeCourse());
        for (Course ver : verUpdate) {
            courseRepository.deleteById(ver.getId());
        }
        courseRepository.deleteById(courseId);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public void submitCourse(SubmitCourseReq req) {
        Course course = courseRepository.findById(req.getId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getStatus() != Status.APPROVED) {
            course.setStatus(Status.PENDING);
            course.setUpdated_at(LocalDateTime.now());
            courseRepository.save(course);
        } else
            throw new AppException(ErrorCode.SUBMIT_COURSE_FAIL);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<Course> getAllForAdmin(AdminCourseParamReq req) {
        PageRequest pageRequest = PageRequest.of(req.getPage() - 1 >= 0 ? req.getPage() - 1 : 0, req.getPageSize());
        Sort sort = Sort.by("title").descending().and(Sort.by("updated_at").descending());
        System.out.println(req.getStatus());
        return courseRepository.findByTitleAndStatus(req.getTitle(),
                req.getStatus(), pageRequest.withSort(sort));
    }

    @PreAuthorize("hasAuthority('STUDENT') || hasAuthority('INSTRUCTOR') || hasAuthority('ADMIN')")
    public Course getOneById(int id) {
        return courseRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void reviewCourse(ReviewCourseReq req) {
        Course currCourse = courseRepository.findById(req.getId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        if (currCourse.getStatus() != Status.PENDING)
            throw new AppException(ErrorCode.COURSE_UPDATE_DENIED);

        if (currCourse.getVersion() != 1) {
            if (req.getStatus() == Status.REJECTED) {
                currCourse.setStatus(Status.REJECTED);
                courseRepository.save(currCourse);
            } else {
                Course originalCourse = courseRepository.findCourseOriginalVersion(currCourse.getCodeCourse());
                originalCourse.setTitle(currCourse.getTitle());
                originalCourse.setDescription(currCourse.getDescription());
                originalCourse.setPrice(currCourse.getPrice());
                originalCourse.setCategory(currCourse.getCategory());

                int idOld;
                Lesson oldLesson;
                Set<Lesson> lessons = new HashSet<>();
                for (Lesson lesson : currCourse.getLessons()) {
                    idOld = lesson.getOld_id();
                    if (idOld == 0) {
                        lesson.setCourse(originalCourse);
                        lessonRepository.save(lesson);
                    } else {
                        oldLesson = lessonRepository.findById(idOld).orElse(null);
                        if (oldLesson != null) {
                            oldLesson.setTitle(lesson.getTitle());
                            oldLesson.setContent(lesson.getContent());
                            oldLesson.setOld_id(0);
                            oldLesson.setUpdated_at(LocalDateTime.now());
                            lessonRepository.save(oldLesson);
                        }
                        lessons.add(lesson);
                    }
                }
                currCourse.setLessons(lessons);
                courseRepository.save(originalCourse);
                courseRepository.delete(currCourse);
            }
        } else {
            currCourse.setStatus(req.getStatus());
            courseRepository.save(currCourse);
        }

    }
}
