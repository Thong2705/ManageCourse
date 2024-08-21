package com.mockproject.group3.service;

import com.mockproject.group3.dto.UsersDTO;
import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.AuthenticationResponse;
import com.mockproject.group3.model.Token;
import com.mockproject.group3.model.Users;
import com.mockproject.group3.repository.TokenRepository;
import com.mockproject.group3.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UsersService usersService;

    private Users user;
    private UsersDTO userDto;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("password");

        userDto = new UsersDTO();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        usersService = new UsersService(usersRepository, authenticationManager, tokenRepository, jwtService);
    }

    @Test
    void testSaveUser() {
        when(usersRepository.save(any(Users.class))).thenReturn(user);

        Users savedUser = usersService.saveUser(userDto);

        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    void testAuthenticateSuccess() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateaAccessToken(any(Users.class))).thenReturn("accessToken");
        when(jwtService.generateaRefreshToken(any(Users.class))).thenReturn("refreshToken");

        AuthenticationResponse response = usersService.authenticate(userDto);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

    @Test
    void testAuthenticateFailure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(BadCredentialsException.class);

        AppException exception = assertThrows(AppException.class, () -> {
            usersService.authenticate(userDto);
        });

        assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
    }

    @Test
    void testAuthenticateFailureBadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(BadCredentialsException.class);

        AppException exception = assertThrows(AppException.class, () -> {
            usersService.authenticate(userDto);
        });

        assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
    }

    @Test
    void testAuthenticateFailureDataIntegrityViolation() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(DataIntegrityViolationException.class);

        AppException exception = assertThrows(AppException.class, () -> {
            usersService.authenticate(userDto);
        });

        assertEquals(ErrorCode.UNKNOWN_ERROR, exception.getErrorCode());
    }

    @Test
    void testRevokeAllTokenByUser() {
        List<Token> tokens = new ArrayList<>();
        Token token = new Token();
        token.setLoggedOut(false);
        tokens.add(token);
        
        when(tokenRepository.findAllAccessTokenByUser(anyInt())).thenReturn(tokens);

        usersService.revokeAllTokenByUser(user);

        assertTrue(token.isLoggedOut());
        verify(tokenRepository, times(1)).saveAll(tokens);
    }

    @Test
    void testSaveUserToken() {
        Token token = new Token();
        token.setAccessToken("accessToken");
        token.setRefreshToken("refreshToken");
        token.setLoggedOut(false);
        token.setUser(user);

        usersService.saveUserToken("accessToken", "refreshToken", user);

        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserDetails userDetails = usersService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
    }

    @Test
    void testLoadUserByUsernameFailure() {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            usersService.loadUserByUsername("test@example.com");
        });
        assertEquals(ErrorCode.USER_NOTFOUND, exception.getErrorCode());

    }

    @Test
    void testGetAllUsers() {
        PaginationParamReq req = new PaginationParamReq();
        req.setPage(1);
        req.setPageSize(10);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Users> expectedPage = mock(Page.class);

        when(usersRepository.findAll(pageRequest)).thenReturn(expectedPage);

        Page<Users> result = usersService.getAllUsers(req);

        assertEquals(expectedPage, result);
        verify(usersRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void testGetAllUsersInvalidPage() {
        PaginationParamReq req = new PaginationParamReq();
        req.setPage(0);
        req.setPageSize(10);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Users> expectedPage = mock(Page.class);

        when(usersRepository.findAll(pageRequest)).thenReturn(expectedPage);

        Page<Users> result = usersService.getAllUsers(req);

        assertEquals(expectedPage, result);
        verify(usersRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void testRefreshToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token");
        when(jwtService.extractEmail(anyString())).thenReturn("test@example.com");
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.isValidRefreshToken(anyString(), any(Users.class))).thenReturn(true);
        when(jwtService.generateaAccessToken(any(Users.class))).thenReturn("newAccessToken");
        when(jwtService.generateaRefreshToken(any(Users.class))).thenReturn("newRefreshToken");

        ResponseEntity result = usersService.refreshToken(request, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        AuthenticationResponse authResponse = (AuthenticationResponse) result.getBody();
        assertNotNull(authResponse);
        assertEquals("newAccessToken", authResponse.getAccessToken());
        assertEquals("newRefreshToken", authResponse.getRefreshToken());
    }

    @Test
    void testUpdateResetPasswordTokenSuccess() {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        String resetToken = "resetToken";
        usersService.updateResetPasswordToken(resetToken, "test@example.com");

        assertEquals(resetToken, user.getResetPasswordToken());
        verify(usersRepository, times(1)).save(user);
    }

    @Test
    void testUpdateResetPasswordTokenUserNotFound() {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        String resetToken = "resetToken";
        AppException exception = assertThrows(AppException.class, () -> {
            usersService.updateResetPasswordToken(resetToken, "test@example.com");
        });

        assertEquals(ErrorCode.USER_NOTFOUND, exception.getErrorCode());
        verify(usersRepository, never()).save(any(Users.class));
    }

    @Test
    void testGetUserByTokenResetPassword() {
        when(usersRepository.findByResetPasswordToken(anyString())).thenReturn(user);

        Users result = usersService.getUserByTokenResetPassword("resetToken");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testUpdateNewPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String newPassword = encoder.encode("newPassword");
        user.setPassword(newPassword);

        usersService.updateNewPassword(user, "newPassword");

        assertNotNull(user.getPassword());
        assertTrue(encoder.matches("newPassword", user.getPassword()));
        assertNull(user.getResetPasswordToken());
        verify(usersRepository, times(1)).save(user);
    }

    @Test
    void testRandomString() {
        String randomString = usersService.RandomString(10);

        assertNotNull(randomString);
        assertEquals(10, randomString.length());
    }

    @Test
    void testRefreshTokenSuccess() {
        String token = "Bearer validToken";
        String username = "test@example.com";
        Users user = new Users();
        user.setEmail(username);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(jwtService.extractEmail("validToken")).thenReturn(username);
        when(usersRepository.findByEmail(username)).thenReturn(Optional.of(user));
        when(jwtService.isValidRefreshToken("validToken", user)).thenReturn(true);
        when(jwtService.generateaAccessToken(user)).thenReturn("newAccessToken");
        when(jwtService.generateaRefreshToken(user)).thenReturn("newRefreshToken");

        ResponseEntity<?> responseEntity = usersService.refreshToken(request, response);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AuthenticationResponse authResponse = (AuthenticationResponse) responseEntity.getBody();
        assertNotNull(authResponse);
        assertEquals("newAccessToken", authResponse.getAccessToken());
        assertEquals("newRefreshToken", authResponse.getRefreshToken());

        verify(tokenRepository).saveAll(anyList());
    }

    @Test
    void testRefreshTokenUnauthorizedNoHeader() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        ResponseEntity<?> responseEntity = usersService.refreshToken(request, response);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    @Test
    void testRefreshTokenUnauthorizedInvalidHeader() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidHeader");

        ResponseEntity<?> responseEntity = usersService.refreshToken(request, response);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    @Test
    void testRefreshTokenUnauthorizedUserNotFound() {
        String token = "Bearer validToken";
        String username = "test@example.com";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(jwtService.extractEmail("validToken")).thenReturn(username);
        when(usersRepository.findByEmail(username)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            usersService.refreshToken(request, response);
        });

        assertEquals(ErrorCode.USER_NOTFOUND, exception.getErrorCode());
    }

    @Test
    void testRefreshTokenUnauthorizedInvalidToken() {
        String token = "Bearer validToken";
        String username = "test@example.com";
        Users user = new Users();
        user.setEmail(username);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(jwtService.extractEmail("validToken")).thenReturn(username);
        when(usersRepository.findByEmail(username)).thenReturn(Optional.of(user));
        when(jwtService.isValidRefreshToken("validToken", user)).thenReturn(false);

        ResponseEntity<?> responseEntity = usersService.refreshToken(request, response);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }
}
