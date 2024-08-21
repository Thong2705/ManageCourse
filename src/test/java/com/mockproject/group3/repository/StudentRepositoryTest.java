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

import com.mockproject.group3.model.Student;

@ExtendWith(MockitoExtension.class)
public class StudentRepositoryTest {

    @Mock
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        // Initial setup if needed
    }

    @Test
    void testFindById() {
        Student student = new Student();
        student.setId(1);
        Optional<Student> optionalStudent = Optional.of(student);

        when(studentRepository.findById(1)).thenReturn(optionalStudent);

        Optional<Student> foundStudent = studentRepository.findById(1);
        assertTrue(foundStudent.isPresent());
        assertEquals(1, foundStudent.get().getId());
    }
}
