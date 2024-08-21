package com.mockproject.group3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.mockproject.group3.model.Users;
import com.mockproject.group3.enums.Role;
import com.mockproject.group3.model.Token;
import com.mockproject.group3.repository.TokenRepository;
import com.mockproject.group3.repository.UsersRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private JwtService jwtService;

    @Captor
    private ArgumentCaptor<Token> tokenCaptor;

    @Mock
    private UserDetails userDetails;

    @Mock
    private UsersRepository usersRepository;

    private Users user;
    private String secretKey;
    private String token;

    @BeforeEach
    void setUp() {
        secretKey = "7fd960bf038b23299b5ca9bb565f4537d9c4aed78b0ab1f4efa53125697becff";
        jwtService.setSecretKey(secretKey);
        jwtService.setAccessTokenExpire(3600000);
        jwtService.setRefreshTokenExpire(7200000);

        user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(Role.STUDENT);
        
        token = jwtService.generateaAccessToken(user);
    }

    @Test
    void testExtractEmail() {
        String email = jwtService.extractEmail(token);
        assertEquals("test@example.com", email);
    }

    @Test
    void testIsValid() {
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(tokenRepository.findByAccessToken(token)).thenReturn(Optional.of(createToken(token, false)));

        boolean isValid = jwtService.isValid(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void testIsValid_InvalidToken() {
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(tokenRepository.findByAccessToken(token)).thenReturn(Optional.empty());

        boolean isValid = jwtService.isValid(token, userDetails);
        assertFalse(isValid);
    }

    @Test
    void testIsValidRefreshToken() {
        when(tokenRepository.findByRefreshToken(token)).thenReturn(Optional.of(createToken(token, false)));

        boolean isValid = jwtService.isValidRefreshToken(token, user);
        assertTrue(isValid);
    }

    @Test
    void testIsValidRefreshToken_InvalidToken() {
        when(tokenRepository.findByRefreshToken(token)).thenReturn(Optional.empty());

        boolean isValid = jwtService.isValidRefreshToken(token, user);
        assertFalse(isValid);
    }

    @Test
    void testGenerateaAccessToken() {
        String generatedToken = jwtService.generateaAccessToken(user);
        assertNotNull(generatedToken);
    }

    @Test
    void testGenerateaRefreshToken() {
        String generatedToken = jwtService.generateaRefreshToken(user);
        assertNotNull(generatedToken);
    }

    @Test
    void testIsTokenExpired() {
        boolean isExpired = jwtService.isTokenExpired(token);
        assertFalse(isExpired);
    }

    @Test
    void testIsTokenExpired_ExpiredToken() {
        // Tạo một token hết hạn
        String expiredToken = Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 hours ago
                .expiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .signWith(getSigninKey())
                .compact();

        // Kiểm tra token hết hạn bằng cách gọi trực tiếp phương thức trong JwtService
        boolean isExpired = jwtService.isTokenExpired(expiredToken);
        assertTrue(isExpired);
    }

    @Test
    void testIsValid_ValidToken() {
        String validToken = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour later
                .signWith(getSigninKey())
                .compact();

        Token tokenEntity = new Token();
        tokenEntity.setAccessToken(validToken);
        tokenEntity.setLoggedOut(false);

        when(tokenRepository.findByAccessToken(validToken)).thenReturn(Optional.of(tokenEntity));

        boolean isValid = jwtService.isValid(validToken, user);

        assertTrue(isValid);
    }

    @Test
    void testIsValid_LoggedOutToken() {
        String validToken = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour later
                .signWith(getSigninKey())
                .compact();

        Token tokenEntity = new Token();
        tokenEntity.setAccessToken(validToken);
        tokenEntity.setLoggedOut(true);

        when(tokenRepository.findByAccessToken(validToken)).thenReturn(Optional.of(tokenEntity));

        boolean isValid = jwtService.isValid(validToken, user);

        assertFalse(isValid);
    }

    @Test
    void testIsValid_ExpiredToken() {
        String expiredToken = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .signWith(getSigninKey())
                .compact();

        Token tokenEntity = new Token();
        tokenEntity.setAccessToken(expiredToken);
        tokenEntity.setLoggedOut(false);

        // No need to stub if the token is expired, it will never reach the repository check.
        boolean isValid = jwtService.isValid(expiredToken, user);

        assertFalse(isValid);
    }

    @Test
    void testIsValid_TokenNotFound() {
        String validToken = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour later
                .signWith(getSigninKey())
                .compact();

        when(tokenRepository.findByAccessToken(validToken)).thenReturn(Optional.empty());

        boolean isValid = jwtService.isValid(validToken, user);

        assertFalse(isValid);
    }

    private Token createToken(String token, boolean isLoggedOut) {
        Token tokenEntity = new Token();
        tokenEntity.setAccessToken(token);
        tokenEntity.setLoggedOut(isLoggedOut);
        return tokenEntity;
    }

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
