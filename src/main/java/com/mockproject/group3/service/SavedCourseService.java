package com.mockproject.group3.service;
import com.mockproject.group3.dto.SavedCourseDTO;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.SavedCourse;
import com.mockproject.group3.model.Student;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.SavedCourseRepository;
import com.mockproject.group3.repository.StudentRepository;
import com.mockproject.group3.utils.GetAuthUserInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@PreAuthorize("hasAuthority('STUDENT')")
public class SavedCourseService {

    private final SavedCourseRepository savedCourseRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    private final GetAuthUserInfo getAuthUserInfo;


    public SavedCourseService(GetAuthUserInfo getAuthUserInfo, StudentRepository studentRepository, CourseRepository courseRepository, SavedCourseRepository savedCourseRepository) {
        this.getAuthUserInfo = getAuthUserInfo;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.savedCourseRepository = savedCourseRepository;
    }

    public SavedCourse createSavedCourse(SavedCourseDTO savedCourseDTO) {
        int studentId = getAuthUserInfo.getAuthUserId();
        try {
            Student student = studentRepository.findById(studentId).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
            Course course = courseRepository.findById(savedCourseDTO.getCourseId()).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
            SavedCourse existSavedCourse = savedCourseRepository.findByCourseIdAndStudentId(savedCourseDTO.getCourseId(), studentId).orElse(null);
            if (existSavedCourse != null) {
                throw new AppException(ErrorCode.SAVED_COURSE_EXIST);
            }
            SavedCourse savedCourse = new SavedCourse();
            savedCourse.setStudent(student);
            savedCourse.setCourse(course);

            return  savedCourseRepository.save(savedCourse);
        }
        catch (Exception e) {
            throw new AppException(ErrorCode.STUDENT_AND_COURSE_EXIST);
        }
    }
    public List<SavedCourse> getAllSavedCourse() {
        return savedCourseRepository.findAll();
    }
    public void deleteSavedCourse(int id) {
        savedCourseRepository.deleteById(id);
    }
}
