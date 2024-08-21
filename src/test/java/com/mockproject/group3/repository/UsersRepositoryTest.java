package com.mockproject.group3.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mockproject.group3.model.Users;

@ExtendWith(MockitoExtension.class)
public class UsersRepositoryTest {

    @Mock
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        // Initial setup if needed
    }

    @Test
    void testFindById() {
        Users user = new Users();
        user.setId(1);
        Optional<Users> optionalUser = Optional.of(user);

        when(usersRepository.findById(1)).thenReturn(optionalUser);

        Optional<Users> foundUser = usersRepository.findById(1);
        assertTrue(foundUser.isPresent());
        assertEquals(1, foundUser.get().getId());
    }

    @Test
    void testFindByVerificationCode() {
        Users user = new Users();
        user.setVerificationCode("1234");

        when(usersRepository.findByVerificationCode("1234")).thenReturn(user);

        Users foundUser = usersRepository.findByVerificationCode("1234");
        assertEquals("1234", foundUser.getVerificationCode());
    }

    @Test
    void testFindByEmail() {
        Users user = new Users();
        user.setEmail("test@example.com");
        Optional<Users> optionalUser = Optional.of(user);

        when(usersRepository.findByEmail("test@example.com")).thenReturn(optionalUser);

        Optional<Users> foundUser = usersRepository.findByEmail("test@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void testFindByResetPasswordToken() {
        Users user = new Users();
        user.setResetPasswordToken("token123");

        when(usersRepository.findByResetPasswordToken("token123")).thenReturn(user);

        Users foundUser = usersRepository.findByResetPasswordToken("token123");
        assertEquals("token123", foundUser.getResetPasswordToken());
    }

    @Test
    void testExistsByEmail() {
        when(usersRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean exists = usersRepository.existsByEmail("test@example.com");
        assertTrue(exists);

        when(usersRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        boolean notExists = usersRepository.existsByEmail("nonexistent@example.com");
        assertFalse(notExists);
    }
}