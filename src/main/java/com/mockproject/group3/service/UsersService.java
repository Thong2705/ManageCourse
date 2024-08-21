package com.mockproject.group3.service;

import java.util.List;
import java.util.Random;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

@Service
public class UsersService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    // private final PasswordEncoder passwordEncoder;
    
    public UsersService(UsersRepository usersRepository, @Lazy AuthenticationManager authenticationManager,
            TokenRepository tokenRepository, JwtService jwtService) {
        this.usersRepository = usersRepository;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        // this.passwordEncoder = passwordEncoder;
    }

    public Users saveUser(UsersDTO usersDto) {
        Users users = new Users();
        users.setEmail(usersDto.getEmail());
        users.setPassword(usersDto.getPassword());
        users.setFull_name(usersDto.getFull_name());
        users.setAddress(usersDto.getAddress());
        users.setPhone(usersDto.getPhone());
        users.setRole(usersDto.getRole());
        users.setBlocked(false);
        return usersRepository.save(users);
    }


    public AuthenticationResponse authenticate(UsersDTO request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            Users user = usersRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

            String accessToken = jwtService.generateaAccessToken(user);
            String refreshToken = jwtService.generateaRefreshToken(user);
            revokeAllTokenByUser(user);
            saveUserToken(accessToken, refreshToken, user);
            return new AuthenticationResponse(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.UNKNOWN_ERROR);
        }
    }

    public void revokeAllTokenByUser(Users user) {
        List<Token> validTokenListByUser = tokenRepository.findAllAccessTokenByUser(user.getId());
        if (!validTokenListByUser.isEmpty()) {
            validTokenListByUser.forEach(t -> t.setLoggedOut(true));
        }
        tokenRepository.saveAll(validTokenListByUser);
    }

    public void saveUserToken(String accesToken, String refreshToken,Users user) {
        Token token = new Token();
        token.setAccessToken(accesToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByEmail(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<Users> getAllUsers(PaginationParamReq req) {
         PageRequest pageRequest = PageRequest.of(req.getPage() - 1 >= 0 ? req.getPage() - 1 : 0, req.getPageSize());
        Sort sort = Sort.by("id").descending();

        return usersRepository.findAll(pageRequest.withSort(sort));
    }

    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    
        String token = authHeader.substring(7);
        String username = jwtService.extractEmail(token);
    
        Users users = usersRepository.findByEmail(username).orElseThrow(()-> new AppException(ErrorCode.USER_NOTFOUND));
    
        if(jwtService.isValidRefreshToken(token, users)) {
            String accessToken = jwtService.generateaAccessToken(users);
            String refreshToken = jwtService.generateaRefreshToken(users);
    
            revokeAllTokenByUser(users);
            saveUserToken (accessToken, refreshToken, users);
    
            return new ResponseEntity<>(new AuthenticationResponse(accessToken, refreshToken), HttpStatus.OK);
    
        }
    
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    public void updateResetPasswordToken(String token, String email){
        Users user = usersRepository.findByEmail(email).orElseThrow(()-> new AppException(ErrorCode.USER_NOTFOUND));
        user.setResetPasswordToken(token);
        usersRepository.save(user);
    }

    public Users getUserByTokenResetPassword (String resetPasswordToken){
        return usersRepository.findByResetPasswordToken(resetPasswordToken);
    }

    public void updateNewPassword (Users user, String newPassword){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setResetPasswordToken(null);
        usersRepository.save(user);
    }
    public String RandomString(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }

        return stringBuilder.toString();
    }
}

