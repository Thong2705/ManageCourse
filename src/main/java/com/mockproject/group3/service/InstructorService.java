package com.mockproject.group3.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import com.mockproject.group3.dto.*;
import com.mockproject.group3.model.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mockproject.group3.dto.ChangePasswordDTO;
import com.mockproject.group3.dto.InstructorDTO;
import com.mockproject.group3.dto.UsersDTO;
import com.mockproject.group3.dto.request.instructor.EarningReq;
import com.mockproject.group3.dto.response.instructor.EarningInstructorRes;
import com.mockproject.group3.enums.Role;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.AuthenticationResponse;
import com.mockproject.group3.model.Instructor;
import com.mockproject.group3.model.Review;
import com.mockproject.group3.model.Token;
import com.mockproject.group3.model.Users;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.InstructorRepository;
import com.mockproject.group3.repository.TokenRepository;
import com.mockproject.group3.repository.UsersRepository;
import com.mockproject.group3.utils.GetAuthUserInfo;
@Service
public class InstructorService {

    private final EnrollmentService enrollmentService;
    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final GetAuthUserInfo getAuthUserInfo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsersRepository usersRepository;
    private TokenRepository tokenRepository;

    public InstructorService(EnrollmentService enrollmentService, InstructorRepository instructorRepository, CourseRepository courseRepository,
                             GetAuthUserInfo getAuthUserInfo, PasswordEncoder passwordEncoder, JwtService jwtService, UsersRepository usersRepository,
                             TokenRepository tokenRepository) {
        this.enrollmentService = enrollmentService;
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        this.getAuthUserInfo = getAuthUserInfo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.usersRepository = usersRepository;
        this.tokenRepository = tokenRepository;
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public List<Instructor> getAllInstructor() {
        return instructorRepository.findAll();
    }

    public AuthenticationResponse saveInstructor(UsersDTO request) {
        Users user = new Users();
        if(usersRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFull_name(request.getFull_name());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());
        user.setRole(Role.INSTRUCTOR);
        user.setVerificationCode(request.getVerificationCode());
        user = usersRepository.save(user);

        Instructor instructor = new Instructor();
        instructor.setUser(user);
        instructor.setInstructor_code("FPTINS" + user.getId());
        instructorRepository.save(instructor);
        
        String accessToken = jwtService.generateaAccessToken(user);
        String refreshToken = jwtService.generateaRefreshToken(user);
        saveUserToken(accessToken, refreshToken, user);
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    private void saveUserToken(String accesToken, String refreshToken,Users user) {
        Token token = new Token();
        token.setAccessToken(accesToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public boolean changePassword(ChangePasswordDTO changePasswordDTO) {
        int instructorId = getAuthUserInfo.getAuthUserId();
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

            if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), instructor.getUser().getPassword())) {
                throw new AppException(ErrorCode.INVALID_CURRENTPASSWORD);
            }
        
            if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), instructor.getUser().getPassword())) {
                throw new AppException(ErrorCode.INVALID_NEWPASSWORD);
            }
        
            if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
                throw new AppException(ErrorCode.INVALID_COMFIRMNEWPASSWORD);
            }
        
            instructor.getUser().setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
            instructorRepository.save(instructor);
            
            return true;
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public Instructor updateProfile(UsersDTO userDto) {
        int instructorId = getAuthUserInfo.getAuthUserId();
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

        instructor.getUser().setFull_name(userDto.getFull_name());
        instructor.getUser().setAddress(userDto.getAddress());
        instructor.getUser().setPhone(userDto.getPhone());

        return instructorRepository.save(instructor);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public List<EarningInstructorRes> viewEarning(EarningReq req) {
        int year = Integer.valueOf(req.getYear());
        int month = Integer.valueOf(req.getMonth());
        LocalDateTime firstDay = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime lastDay = YearMonth.of(year, month).atEndOfMonth().plusDays(1).atStartOfDay();

        int userId = getAuthUserInfo.getAuthUserId();
        Instructor instructor = instructorRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));

        return courseRepository.earningAnalytics(firstDay, lastDay, instructor.getId());
    }

    public List<Review> findReviewsByInstructorAndCourseId(int courseId) {
        int instructorId = getAuthUserInfo.getAuthUserId();
        return instructorRepository.findReviewsByInstructorAndCourseId(instructorId, courseId);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    public List<InstructorDTO> searchInstructorDTO(String name){
        List<InstructorDTO> listInstructors = new ArrayList<InstructorDTO>() ;
        List<Users> listUsers = usersRepository.findAll();
        for (Users user:listUsers){
            InstructorDTO insDTO = new InstructorDTO();
            if (user.getRole().equals(Role.INSTRUCTOR) && name.equalsIgnoreCase(user.getFull_name())){
                insDTO.setInstructorName(user.getFull_name());
                insDTO.setFee(user.getInstructor().getFee());
                insDTO.setInstructor_code(user.getInstructor().getInstructor_code());
                insDTO.setProfession_experience(user.getInstructor().getProfession_experience());
                insDTO.setPayout(user.getInstructor().getPayouts());
                insDTO.setSubscription(user.getInstructor().getSubscriptions());
                listInstructors.add(insDTO);
            }
            
        }
        return listInstructors;
        
    }

    public List<CourseDTO> getDashBoard(){
        List<CourseDTO> listCourses = new ArrayList<CourseDTO>();
        int instructorId = getAuthUserInfo.getAuthUserId();
        Instructor instructor = instructorRepository.findById(instructorId).orElse(null);

        for (Course course:instructor.getCourses()){
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setCourseId(course.getId());
            courseDTO.setCourseName(course.getTitle());
            courseDTO.setCourseCode(course.getCodeCourse());
            courseDTO.setCourseStatus(course.getStatus());
            courseDTO.setCourseRate(enrollmentService.getCompletedLessonRate(course.getId()));
            listCourses.add(courseDTO);
        }
        return listCourses;
    }
}
