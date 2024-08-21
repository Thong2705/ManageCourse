package com.mockproject.group3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

import com.mockproject.group3.dto.CourseDTO;
import com.mockproject.group3.enums.Status;
import com.mockproject.group3.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mockproject.group3.dto.ChangePasswordDTO;
import com.mockproject.group3.dto.InstructorDTO;
import com.mockproject.group3.dto.UsersDTO;
import com.mockproject.group3.dto.request.instructor.EarningReq;
import com.mockproject.group3.dto.response.instructor.EarningInstructorRes;
import com.mockproject.group3.enums.Role;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.InstructorRepository;
import com.mockproject.group3.repository.TokenRepository;
import com.mockproject.group3.repository.UsersRepository;
import com.mockproject.group3.utils.GetAuthUserInfo;

@ExtendWith(MockitoExtension.class)
public class InstructorServiceTest {

    @Mock
    private EnrollmentService enrollmentService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private GetAuthUserInfo getAuthUserInfo;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private InstructorService instructorService;

    @Captor
    private ArgumentCaptor<Users> usersCaptor;

    @Captor
    private ArgumentCaptor<Instructor> instructorCaptor;

    @Captor
    private ArgumentCaptor<Token> tokenCaptor;

    @Captor
    private ArgumentCaptor<LocalDateTime> dateTimeCaptor;

    private UsersDTO usersDTO;
    private ChangePasswordDTO changePasswordDTO;
    private Users user;
    private Instructor instructor;

    @BeforeEach
    void setUp() {
        usersDTO = new UsersDTO();
        usersDTO.setEmail("test@example.com");
        usersDTO.setPassword("password");
        usersDTO.setFull_name("Test User");
        usersDTO.setAddress("123 Test Street");
        usersDTO.setPhone("1234567890");
        usersDTO.setVerificationCode("12345");

        changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword("currentPassword");
        changePasswordDTO.setNewPassword("newPassword");
        changePasswordDTO.setConfirmPassword("newPassword");

        user = new Users();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setPassword("encodedCurrentPassword");
        user.setFull_name("Test User");
        user.setAddress("123 Test St");
        user.setPhone("1234567890");
        user.setVerificationCode("123456");

        instructor = new Instructor();
        instructor.setId(1);
        instructor.setUser(user);
        instructor.setInstructor_code("FPTINS1");
    }

    @Test
    void testSaveInstructor_EmailAlreadyExists() {
        when(usersRepository.existsByEmail(usersDTO.getEmail())).thenReturn(true);
        
        AppException exception = assertThrows(AppException.class, () -> {
            instructorService.saveInstructor(usersDTO);
        });
        
        assertEquals(ErrorCode.EMAIL_EXISTED, exception.getErrorCode());
    }

    @Test
    void testSaveInstructor_Success() {
        when(usersRepository.existsByEmail(usersDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(usersDTO.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateaAccessToken(any(Users.class))).thenReturn("accessToken");
        when(jwtService.generateaRefreshToken(any(Users.class))).thenReturn("refreshToken");
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users user = invocation.getArgument(0);
            user.setId(1); // Set a non-null ID to avoid NullPointerException
            return user;
        });

        AuthenticationResponse response = instructorService.saveInstructor(usersDTO);

        verify(usersRepository).save(usersCaptor.capture());
        verify(instructorRepository).save(instructorCaptor.capture());
        verify(tokenRepository).save(tokenCaptor.capture());

        Users savedUser = usersCaptor.getValue();
        Instructor savedInstructor = instructorCaptor.getValue();
        Token savedToken = tokenCaptor.getValue();

        assertEquals(usersDTO.getEmail(), savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(usersDTO.getFull_name(), savedUser.getFull_name());
        assertEquals(usersDTO.getAddress(), savedUser.getAddress());
        assertEquals(usersDTO.getPhone(), savedUser.getPhone());
        assertEquals(Role.INSTRUCTOR, savedUser.getRole());
        assertEquals(usersDTO.getVerificationCode(), savedUser.getVerificationCode());
        assertEquals(savedUser, savedInstructor.getUser());
        assertEquals("accessToken", savedToken.getAccessToken());
        assertEquals("refreshToken", savedToken.getRefreshToken());

        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    void testChangePassword_InvalidCurrentPassword() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(instructorRepository.findById(1)).thenReturn(Optional.of(instructor));
        when(passwordEncoder.matches("currentPassword", "encodedCurrentPassword")).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> {
            instructorService.changePassword(changePasswordDTO);
        });

        assertEquals(ErrorCode.INVALID_CURRENTPASSWORD, exception.getErrorCode());

        verify(instructorRepository).findById(1);
        verify(passwordEncoder).matches("currentPassword", "encodedCurrentPassword");
        verify(instructorRepository, never()).save(any(Instructor.class));
    }

    @Test
    void testChangePassword_instructorNotFound() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(instructorRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            instructorService.changePassword(changePasswordDTO);
        });

        assertEquals(ErrorCode.INSTRUCTOR_NOT_FOUND, exception.getErrorCode());

        verify(instructorRepository).findById(1);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(instructorRepository, never()).save(any(Instructor.class));
    }

    @Test
    void testChangePassword_InvalidNewPassword() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(instructorRepository.findById(1)).thenReturn(Optional.of(instructor));
        when(passwordEncoder.matches("currentPassword", "encodedCurrentPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword", "encodedCurrentPassword")).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> {
            instructorService.changePassword(changePasswordDTO);
        });

        assertEquals(ErrorCode.INVALID_NEWPASSWORD, exception.getErrorCode());

        verify(instructorRepository).findById(1);
        verify(passwordEncoder).matches("currentPassword", "encodedCurrentPassword");
        verify(passwordEncoder).matches("newPassword", "encodedCurrentPassword");
        verify(instructorRepository, never()).save(any(Instructor.class));
    }

    @Test
    void testChangePassword_InvalidConfirmPassword() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(instructorRepository.findById(1)).thenReturn(Optional.of(instructor));
        when(passwordEncoder.matches("currentPassword", "encodedCurrentPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword", "encodedCurrentPassword")).thenReturn(false);

        changePasswordDTO.setConfirmPassword("differentPassword");

        AppException exception = assertThrows(AppException.class, () -> {
            instructorService.changePassword(changePasswordDTO);
        });

        assertEquals(ErrorCode.INVALID_COMFIRMNEWPASSWORD, exception.getErrorCode());

        verify(instructorRepository).findById(1);
        verify(passwordEncoder).matches("currentPassword", "encodedCurrentPassword");
        verify(instructorRepository, never()).save(any(Instructor.class));
    }

    @Test
    void testChangePassword_Success() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(instructorRepository.findById(1)).thenReturn(Optional.of(instructor));
        when(passwordEncoder.matches("currentPassword", "encodedCurrentPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword", "encodedCurrentPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        boolean result = instructorService.changePassword(changePasswordDTO);

        assertEquals(true, result);
        verify(instructorRepository).findById(1);
        verify(passwordEncoder).matches("currentPassword", "encodedCurrentPassword");
        verify(passwordEncoder).matches("newPassword", "encodedCurrentPassword");
        verify(passwordEncoder).encode("newPassword");
        verify(instructorRepository).save(instructor);
    }

    @Test
    void testUpdateProfile_Success() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(instructorRepository.findById(1)).thenReturn(Optional.of(instructor));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        UsersDTO updatedUserDTO = new UsersDTO();
        updatedUserDTO.setFull_name("Updated User");
        updatedUserDTO.setAddress("456 Updated St");
        updatedUserDTO.setPhone("0987654321");

        Instructor updatedinstructor = instructorService.updateProfile(updatedUserDTO);

        assertEquals("Updated User", updatedinstructor.getUser().getFull_name());
        assertEquals("456 Updated St", updatedinstructor.getUser().getAddress());
        assertEquals("0987654321", updatedinstructor.getUser().getPhone());

        verify(instructorRepository).findById(1);
        verify(instructorRepository).save(instructor);
    }

    @Test
    void testUpdateProfile_instructorNotFound() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(instructorRepository.findById(1)).thenReturn(Optional.empty());

        UsersDTO updatedUserDTO = new UsersDTO();
        updatedUserDTO.setFull_name("Updated User");
        updatedUserDTO.setAddress("456 Updated St");
        updatedUserDTO.setPhone("0987654321");

        AppException exception = assertThrows(AppException.class, () -> {
            instructorService.updateProfile(updatedUserDTO);
        });

        assertEquals(ErrorCode.INSTRUCTOR_NOT_FOUND, exception.getErrorCode());

        verify(instructorRepository).findById(1);
        verify(instructorRepository, never()).save(any(Instructor.class));
    }

    @Test
    void testGetAllInstructor_Success() {
        List<Instructor> instructors = new ArrayList<>();
        instructors.add(instructor);

        when(instructorRepository.findAll()).thenReturn(instructors);

        List<Instructor> allInstructors = instructorService.getAllInstructor();

        assertEquals(1, allInstructors.size());
        assertEquals(instructor, allInstructors.get(0));

        verify(instructorRepository).findAll();
    }

    @Test
    void testViewEarning_Success() {
        EarningReq req = new EarningReq();
        req.setYear("2023");
        req.setMonth("07");
        int year = Integer.valueOf(req.getYear());
        int month = Integer.valueOf(req.getMonth());
        LocalDateTime firstDay = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime lastDay = YearMonth.of(year, month).atEndOfMonth().plusDays(1).atStartOfDay();
    
        int userId = 1;
        Instructor instructor = new Instructor();
        instructor.setId(userId);
    
        List<EarningInstructorRes> earnings = new ArrayList<>();
        earnings.add(new EarningInstructorRes());
    
        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));
        when(courseRepository.earningAnalytics(any(LocalDateTime.class), any(LocalDateTime.class), anyInt())).thenReturn(earnings);
    
        List<EarningInstructorRes> result = instructorService.viewEarning(req);
    
        assertEquals(earnings, result);
        verify(getAuthUserInfo).getAuthUserId();
        verify(instructorRepository).findById(userId);
        
        // Capture arguments and verify
        ArgumentCaptor<LocalDateTime> captorStart = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> captorEnd = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<Integer> captorUserId = ArgumentCaptor.forClass(Integer.class);
        
        verify(courseRepository).earningAnalytics(captorStart.capture(), captorEnd.capture(), captorUserId.capture());
        
        assertEquals(firstDay, captorStart.getValue());
        assertEquals(lastDay, captorEnd.getValue());
        assertEquals(userId, captorUserId.getValue().intValue());
    }
    

    @Test
    void testFindReviewsByInstructorAndCourseId_Success() {
        int courseId = 1;
        int instructorId = 1;

        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review());

        when(getAuthUserInfo.getAuthUserId()).thenReturn(instructorId);
        when(instructorRepository.findReviewsByInstructorAndCourseId(instructorId, courseId)).thenReturn(reviews);

        List<Review> result = instructorService.findReviewsByInstructorAndCourseId(courseId);

        assertEquals(reviews, result);
        verify(getAuthUserInfo).getAuthUserId();
        verify(instructorRepository).findReviewsByInstructorAndCourseId(instructorId, courseId);
    }

    @Test
    void testSearchInstructorDTO_Success() {
        String name = "Test Instructor";
        Users user = new Users();
        user.setFull_name(name);
        user.setRole(Role.INSTRUCTOR);

        Instructor instructor = new Instructor();
        instructor.setUser(user);
        instructor.setFee(100.0);
        instructor.setInstructor_code("INS123");
        instructor.setProfession_experience("10 years");
        instructor.setPayouts(new HashSet<>());
        instructor.setSubscriptions(new HashSet<>());

        user.setInstructor(instructor);

        List<Users> usersList = new ArrayList<>();
        usersList.add(user);

        when(usersRepository.findAll()).thenReturn(usersList);

        List<InstructorDTO> result = instructorService.searchInstructorDTO(name);

        assertEquals(1, result.size());
        InstructorDTO dto = result.get(0);
        assertEquals(name, dto.getInstructorName());
        assertEquals(100.0, dto.getFee());
        assertEquals("INS123", dto.getInstructor_code());
        assertEquals("10 years", dto.getProfession_experience());
        assertEquals(0, dto.getPayout().size());
        assertEquals(0, dto.getSubscription().size());

        verify(usersRepository).findAll();
    }

        @Test
    void testSearchInstructorDTO_NoMatch() {
        // Sample data
        String searchName = "Non-Existent Instructor";

        Users instructorUser = new Users();
        instructorUser.setFull_name("Test Instructor");
        instructorUser.setRole(Role.INSTRUCTOR);

        Instructor instructor = new Instructor();
        instructor.setFee(500);
        instructor.setInstructor_code("INS123");
        instructor.setProfession_experience("5 years");
        instructor.setPayouts(new HashSet<>());
        instructor.setSubscriptions(new HashSet<>());

        instructorUser.setInstructor(instructor);

        List<Users> usersList = new ArrayList<>();
        usersList.add(instructorUser);

        when(usersRepository.findAll()).thenReturn(usersList);

        // Call the method to test
        List<InstructorDTO> result = instructorService.searchInstructorDTO(searchName);

        // Verify the results
        assertTrue(result.isEmpty());

        verify(usersRepository).findAll();
    }

    @Test
    void testSearchInstructorDTO_RoleMismatch() {
        // Sample data
        String searchName = "Test Instructor";

        Users studentUser = new Users();
        studentUser.setFull_name(searchName);
        studentUser.setRole(Role.STUDENT);

        List<Users> usersList = new ArrayList<>();
        usersList.add(studentUser);

        when(usersRepository.findAll()).thenReturn(usersList);

        // Call the method to test
        List<InstructorDTO> result = instructorService.searchInstructorDTO(searchName);

        // Verify the results
        assertTrue(result.isEmpty());

        verify(usersRepository).findAll();
    }


    @Test
    void testViewEarning_InstructorNotFound() {
        EarningReq req = new EarningReq();
        req.setYear("2023");
        req.setMonth("07");
    
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(instructorRepository.findById(1)).thenReturn(Optional.empty());
    
        AppException exception = assertThrows(AppException.class, () -> {
            instructorService.viewEarning(req);
        });
    
        assertEquals(ErrorCode.INSTRUCTOR_NOT_FOUND, exception.getErrorCode());
    
        verify(getAuthUserInfo).getAuthUserId();
        verify(instructorRepository).findById(1);
    }

    @Test
    void testGetDashBoard() {
        int instructorId = 1;

        // Giả lập hành vi của các thành phần mock
        when(getAuthUserInfo.getAuthUserId()).thenReturn(instructorId);

        Instructor instructor = new Instructor();
        instructor.setId(instructorId);
        Course course = new Course();
        course.setId(1);
        course.setTitle("Test Course");
        course.setCodeCourse("TC101");
        course.setStatus(Status.APPROVED);
        Set<Course> courseSet = new HashSet<>();
        courseSet.add(course);
        instructor.setCourses(courseSet);

        // Sử dụng doReturn để tránh Strict stubbing argument mismatch
        doReturn(Optional.of(instructor)).when(instructorRepository).findById(instructorId);
        when(enrollmentService.getCompletedLessonRate(course.getId())).thenReturn(80.0);

        // Gọi phương thức cần test
        List<CourseDTO> courseDTOList = instructorService.getDashBoard();

        // Kiểm tra kết quả
        assertNotNull(courseDTOList);
        assertEquals(1, courseDTOList.size());
        CourseDTO courseDTO = courseDTOList.get(0);
        assertEquals(course.getId(), courseDTO.getCourseId());
        assertEquals(course.getTitle(), courseDTO.getCourseName());
        assertEquals(course.getCodeCourse(), courseDTO.getCourseCode());
        assertEquals(course.getStatus(), courseDTO.getCourseStatus());
        assertEquals(80, courseDTO.getCourseRate());
    }

}
