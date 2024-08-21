package com.mockproject.group3.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.mockproject.group3.enums.Status;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Users;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.UsersRepository;

@Service
public class AdminService {
    private final CourseRepository courseRepository;
    private final UsersRepository usersRepository;

    public AdminService(CourseRepository courseRepository,
            UsersRepository usersRepository) {
        this.courseRepository = courseRepository;
        this.usersRepository = usersRepository;
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    public Users setBlockUsers(int userId){
        Users user = usersRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        if (user.isBlocked()==true){
            user.setBlocked(false);
            
        }
        else{
            user.setBlocked(true);
        }
        return usersRepository.save(user);
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    public Course setStatusCourse(int courseId, Status status){
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        course.setStatus(status);

        return courseRepository.save(course);
    }

}
