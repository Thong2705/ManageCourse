package com.mockproject.group3.service;

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

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsersRepository usersRepository;
    private final TokenRepository tokenRepository;
    private final GetAuthUserInfo getAuthUserInfo;

    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder, 
                          JwtService jwtService, UsersRepository usersRepository, 
                          TokenRepository tokenRepository, GetAuthUserInfo getAuthUserInfo) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.usersRepository = usersRepository;
        this.tokenRepository = tokenRepository;
        this.getAuthUserInfo = getAuthUserInfo;
    }
    public AuthenticationResponse saveStudent(UsersDTO request) {
        Users user = new Users();
        if(usersRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFull_name(request.getFull_name());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());
        user.setRole(Role.STUDENT);
        user.setVerificationCode(request.getVerificationCode());
        user = usersRepository.save(user);

        Student student = new Student();
        student.setUser(user);
        student.setStudent_code("FPTSTU" + user.getId()); // Thiết lập mã sinh viên dựa trên ID người dùng mới
        studentRepository.save(student);
        
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

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Student> getAllStudent() {
        return studentRepository.findAll();
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    public boolean changePassword(ChangePasswordDTO changePasswordDTO) {
        int studentId = getAuthUserInfo.getAuthUserId();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
    
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), student.getUser().getPassword())) {
            throw new AppException(ErrorCode.INVALID_CURRENTPASSWORD);
        }
    
        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), student.getUser().getPassword())) {
            throw new AppException(ErrorCode.INVALID_NEWPASSWORD);
        }
    
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new AppException(ErrorCode.INVALID_COMFIRMNEWPASSWORD);
        }
    
        student.getUser().setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        studentRepository.save(student);
    
        return true;
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    public Student updateProfile(UsersDTO userDto){
        int student_id = getAuthUserInfo.getAuthUserId();
        Student student = studentRepository.findById(student_id).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        
        student.getUser().setFull_name(userDto.getFull_name());
        student.getUser().setAddress(userDto.getAddress());
        student.getUser().setPhone(userDto.getPhone());
        
        return studentRepository.save(student);
        
    }

    public Student getStudentById(int id) {
        return studentRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

    }

}
