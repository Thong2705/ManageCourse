package com.mockproject.group3.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mockproject.group3.model.Users;
import com.mockproject.group3.repository.TokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.access-token-expiration}")
    private long accessTokenExpire;

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpire;
    @Autowired
    private TokenRepository tokenRepository;

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isValid(String token, UserDetails user) {
        try{
        String email = extractEmail(token);

        boolean isValidToken = tokenRepository.findByAccessToken(token).map(t -> !t.isLoggedOut()).orElse(false);

        return (email.equals(user.getUsername())) && !isTokenExpired(token) && isValidToken;
        }catch(ExpiredJwtException e){
            return false;
        }
    }

    public boolean isValidRefreshToken(String token, Users users) {
        String email = extractEmail(token);

        boolean isValidRefreshToken = tokenRepository.findByRefreshToken(token).map(t -> !t.isLoggedOut())
                .orElse(false);
        return (email.equals(users.getUsername())) && !isTokenExpired(token) && isValidRefreshToken;
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // Token đã hết hạn
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extracAllClaims(token);
        return resolver.apply(claims);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extracAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateaAccessToken(Users user) {
        return generateToken(user, accessTokenExpire);

    }

    public String generateaRefreshToken(Users user) {
        return generateToken(user, refreshTokenExpire);

    }

    private String generateToken(Users user, long expireTime) {
        String token = Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .claim("scope", user.getRole().name())
                .signWith(getSigninKey())
                .compact();

        return token;
    }

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }


    public void setAccessTokenExpire(long accessTokenExpire) {
        this.accessTokenExpire = accessTokenExpire;
    }


    public void setRefreshTokenExpire(long refreshTokenExpire) {
        this.refreshTokenExpire = refreshTokenExpire;
    }


    public void setTokenRepository(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    
}
