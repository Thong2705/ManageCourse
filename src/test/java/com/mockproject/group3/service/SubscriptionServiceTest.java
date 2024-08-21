package com.mockproject.group3.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.dto.request.subscription.SubscriptionReq;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Instructor;
import com.mockproject.group3.model.Student;
import com.mockproject.group3.model.Subscription;
import com.mockproject.group3.repository.InstructorRepository;
import com.mockproject.group3.repository.StudentRepository;
import com.mockproject.group3.repository.SubscriptionRepository;
import com.mockproject.group3.utils.GetAuthUserInfo;

@SpringBootTest
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private GetAuthUserInfo getAuthUserInfo;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllSubscription() {
        PaginationParamReq req = new PaginationParamReq();
        req.setPage(2);
        req.setPageSize(10);

        PageRequest expectedPageRequest = PageRequest.of(1, 10, Sort.by("id").descending());
        Page<Subscription> expectedPage = new PageImpl<>(Collections.emptyList());

        when(subscriptionRepository.findAll(eq(expectedPageRequest)))
                .thenReturn(expectedPage);

        Page<Subscription> result = subscriptionService.getAllSubcription(req);

        verify(subscriptionRepository).findAll(eq(expectedPageRequest));
        assertThat(result).isEqualTo(expectedPage);

        req.setPage(0);

        expectedPageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
        expectedPage = new PageImpl<>(Collections.emptyList());

        when(subscriptionRepository.findAll(eq(expectedPageRequest)))
                .thenReturn(expectedPage);

        result = subscriptionService.getAllSubcription(req);

        verify(subscriptionRepository).findAll(eq(expectedPageRequest));
        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    public void testSubscription() {
        SubscriptionReq req = new SubscriptionReq();
        req.setIdInstructor(1);

        Instructor instructor = new Instructor();
        instructor.setId(1);

        Student student = new Student();
        student.setId(1);

        Subscription subscription = new Subscription();
        subscription.setInstructor(instructor);
        subscription.setStudent(student);

        when(instructorRepository.findById(1)).thenReturn(Optional.of(instructor));
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        Subscription result = subscriptionService.subscription(req);

        assertThat(result).isNotNull();
        assertThat(result.getInstructor()).isEqualTo(instructor);
        assertThat(result.getStudent()).isEqualTo(student);

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository).save(subscriptionCaptor.capture());
        Subscription savedSubscription = subscriptionCaptor.getValue();

        assertThat(savedSubscription.getInstructor()).isEqualTo(instructor);
        assertThat(savedSubscription.getStudent()).isEqualTo(student);
    }

    @Test
    public void testSubscriptionInstructorNotFound() {
        SubscriptionReq req = new SubscriptionReq();
        req.setIdInstructor(1);

        when(instructorRepository.findById(req.getIdInstructor())).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> {
            subscriptionService.subscription(req);
        });

        verify(instructorRepository).findById(req.getIdInstructor());
        verify(studentRepository, never()).findById(anyInt());
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    public void testSubscriptionStudentNotFound() {
        SubscriptionReq req = new SubscriptionReq();
        req.setIdInstructor(1);

        Instructor instructor = new Instructor();
        instructor.setId(1);

        when(instructorRepository.findById(req.getIdInstructor())).thenReturn(Optional.of(instructor));
        when(getAuthUserInfo.getAuthUserId()).thenReturn(2);
        when(studentRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> {
            subscriptionService.subscription(req);
        });

        verify(instructorRepository).findById(req.getIdInstructor());
        verify(studentRepository).findById(2);
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    public void testUnSubscription() {
        int subscriptionId = 1;
        int studentId = 1;

        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);
        Student student = new Student();
        student.setId(studentId);
        subscription.setStudent(student);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(getAuthUserInfo.getAuthUserId()).thenReturn(studentId);

        subscriptionService.unSubscription(subscriptionId);

        verify(subscriptionRepository).delete(subscription);
    }

    @Test
    public void testUnSubscriptionSubscriptionNotFound() {
        int subscriptionId = 1;
        int studentId = 2;

        when(getAuthUserInfo.getAuthUserId()).thenReturn(studentId);
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> {
            subscriptionService.unSubscription(subscriptionId);
        });

        verify(subscriptionRepository).findById(subscriptionId);
        verify(subscriptionRepository, never()).delete(any());
    }

    @Test
    public void testUnSubscriptionWithInvalidStudent() {
        int subscriptionId = 1;
        int studentId = 1;

        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);
        Student student = new Student();
        student.setId(2);
        subscription.setStudent(student);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(getAuthUserInfo.getAuthUserId()).thenReturn(studentId);

        assertThatThrownBy(() -> subscriptionService.unSubscription(subscriptionId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.ACTION_NOT_ALLOW.getMessage());
    }
}
