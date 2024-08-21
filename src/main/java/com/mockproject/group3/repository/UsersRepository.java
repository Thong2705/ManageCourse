package com.mockproject.group3.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mockproject.group3.model.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findById(int userId);
    Users findByVerificationCode(String code);
    Optional<Users> findByEmail(String email);
    Users findByResetPasswordToken(String token);
    boolean existsByEmail(String email);
}