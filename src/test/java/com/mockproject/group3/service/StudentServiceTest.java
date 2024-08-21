package com.mockproject.group3.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mockproject.group3.dto.ChangePasswordDTO;
import com.mockproject.group3.dto.UsersDTO;
import com.mockproject.group3.enums.Role;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.AuthenticationResponse;
import com.mockproject.group3.model.Student;
import com.mockproject.group3.model.Token;
import com.mockproject.group3.model.Users;
import com.mockproject.group3.repository.StudentRepository;
import com.mockproject.group3.repository.TokenRepository;
import com.mockproject.group3.repository.UsersRepository;
import com.mockproject.group3.utils.GetAuthUserInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

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

    @InjectMocks
    private StudentService studentService;

    @Captor
    private ArgumentCaptor<Users> usersCaptor;

    @Captor
    private ArgumentCaptor<Student> studentCaptor;

    @Captor
    private ArgumentCaptor<Token> tokenCaptor;

    private UsersDTO usersDTO;
    private ChangePasswordDTO changePasswordDTO;
    private Users user;
    private Student student;

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

        student = new Student();
        student.setId(1);
        student.setUser(user);
        student.setStudent_code("FPTSTU1");
    }

    @Test
    void testSaveStudent_EmailAlreadyExists() {
        when(usersRepository.existsByEmail(usersDTO.getEmail())).thenReturn(true);
        
        AppException exception = assertThrows(AppException.class, () -> {
            studentService.saveStudent(usersDTO);
        });
        
        assertEquals(ErrorCode.EMAIL_EXISTED, exception.getErrorCode());
    }

    @Test
    void testSaveStudent_Success() {
        when(usersRepository.existsByEmail(usersDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(usersDTO.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateaAccessToken(any(Users.class))).thenReturn("accessToken");
        when(jwtService.generateaRefreshToken(any(Users.class))).thenReturn("refreshToken");
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users user = invocation.getArgument(0);
            user.setId(1); // Set a non-null ID to avoid NullPointerException
            return user;
        });

        AuthenticationResponse response = studentService.saveStudent(usersDTO);

        verify(usersRepository).save(usersCaptor.capture());
        verify(studentRepository).save(studentCaptor.capture());
        verify(tokenRepository).save(tokenCaptor.capture());

        Users savedUser = usersCaptor.getValue();
        Student savedStudent = studentCaptor.getValue();
        Token savedToken = tokenCaptor.getValue();

        assertEquals(usersDTO.getEmail(), savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(usersDTO.getFull_name(), savedUser.getFull_name());
        assertEquals(usersDTO.getAddress(), savedUser.getAddress());
        assertEquals(usersDTO.getPhone(), savedUser.getPhone());
        assertEquals(Role.STUDENT, savedUser.getRole());
        assertEquals(usersDTO.getVerificationCode(), savedUser.getVerificationCode());
        assertEquals(savedUser, savedStudent.getUser());
        assertEquals("accessToken", savedToken.getAccessToken());
        assertEquals("refreshToken", savedToken.getRefreshToken());

        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    void testChangePassword_InvalidCurrentPassword() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("currentPassword", "encodedCurrentPassword")).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> {
            studentService.changePassword(changePasswordDTO);
        });

        assertEquals(ErrorCode.INVALID_CURRENTPASSWORD, exception.getErrorCode());

        verify(studentRepository).findById(1);
        verify(passwordEncoder).matches("currentPassword", "encodedCurrentPassword");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testChangePassword_StudentNotFound() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            studentService.changePassword(changePasswordDTO);
        });

        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());

        verify(studentRepository).findById(1);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testChangePassword_InvalidNewPassword() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("currentPassword", "encodedCurrentPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword", "encodedCurrentPassword")).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> {
            studentService.changePassword(changePasswordDTO);
        });

        assertEquals(ErrorCode.INVALID_NEWPASSWORD, exception.getErrorCode());

        verify(studentRepository).findById(1);
        verify(passwordEncoder).matches("currentPassword", "encodedCurrentPassword");
        verify(passwordEncoder).matches("newPassword", "encodedCurrentPassword");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testChangePassword_InvalidConfirmPassword() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("currentPassword", "encodedCurrentPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword", "encodedCurrentPassword")).thenReturn(false);

        changePasswordDTO.setConfirmPassword("differentPassword");

        AppException exception = assertThrows(AppException.class, () -> {
            studentService.changePassword(changePasswordDTO);
        });

        assertEquals(ErrorCode.INVALID_COMFIRMNEWPASSWORD, exception.getErrorCode());

        verify(studentRepository).findById(1);
        verify(passwordEncoder).matches("currentPassword", "encodedCurrentPassword");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testChangePassword_Success() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("currentPassword", "encodedCurrentPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword", "encodedCurrentPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        boolean result = studentService.changePassword(changePasswordDTO);

        assertEquals(true, result);
        verify(studentRepository).findById(1);
        verify(passwordEncoder).matches("currentPassword", "encodedCurrentPassword");
        verify(passwordEncoder).matches("newPassword", "encodedCurrentPassword");
        verify(passwordEncoder).encode("newPassword");
        verify(studentRepository).save(student);
    }

    @Test
    void testUpdateProfile_Success() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        UsersDTO updatedUserDTO = new UsersDTO();
        updatedUserDTO.setFull_name("Updated User");
        updatedUserDTO.setAddress("456 Updated St");
        updatedUserDTO.setPhone("0987654321");

        Student updatedStudent = studentService.updateProfile(updatedUserDTO);

        assertEquals("Updated User", updatedStudent.getUser().getFull_name());
        assertEquals("456 Updated St", updatedStudent.getUser().getAddress());
        assertEquals("0987654321", updatedStudent.getUser().getPhone());

        verify(studentRepository).findById(1);
        verify(studentRepository).save(student);
    }

    @Test
    void testUpdateProfile_StudentNotFound() {
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.empty());

        UsersDTO updatedUserDTO = new UsersDTO();
        updatedUserDTO.setFull_name("Updated User");
        updatedUserDTO.setAddress("456 Updated St");
        updatedUserDTO.setPhone("0987654321");

        AppException exception = assertThrows(AppException.class, () -> {
            studentService.updateProfile(updatedUserDTO);
        });

        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());

        verify(studentRepository).findById(1);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testGetStudentById_Success() {
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));

        Student foundStudent = studentService.getStudentById(1);

        assertEquals(1, foundStudent.getId());

        verify(studentRepository).findById(1);
    }

    @Test
    void testGetStudentById_StudentNotFound() {
        when(studentRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            studentService.getStudentById(1);
        });

        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());

        verify(studentRepository).findById(1);
    }

    @Test
    void testGetAllStudent_Success() {
        List<Student> students = new ArrayList<>();
        students.add(student);

        when(studentRepository.findAll()).thenReturn(students);

        List<Student> allStudents = studentService.getAllStudent();

        assertEquals(1, allStudents.size());
        assertEquals(student, allStudents.get(0));

        verify(studentRepository).findAll();
    }
}
