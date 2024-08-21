package com.mockproject.group3.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;

import com.mockproject.group3.enums.Template;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Instructor;
import com.mockproject.group3.model.Payout;
import com.mockproject.group3.model.Users;
import com.mockproject.group3.repository.InstructorRepository;
import com.mockproject.group3.repository.PayoutRepository;
import com.mockproject.group3.utils.GetAuthUserInfo;

public class PayoutServiceTest {
    @Mock
    private PayoutRepository payoutRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private GetAuthUserInfo getAuthUserInfo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PayoutService payoutService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testWithdrawSuccess() {
        int userId = 1;
        Instructor instructor = new Instructor();
        instructor.setFee(100.0);
        instructor.setUser(new Users());
        instructor.getUser().setEmail("instructor@example.com");

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));

        payoutService.withdraw();

        verify(instructorRepository).findById(userId);
        verify(payoutRepository).save(any(Payout.class));
        verify(instructorRepository).save(instructor);
        verify(emailService).sendPayoutEmail("instructor@example.com", "Withdraw Successfully", 100.0,
                Template.PAYOUT_TEMPLATE.getTemplateName());

        assertThat(instructor.getFee()).isEqualTo(0.0);
    }

    @Test
    public void testWithdrawInsufficientBalance() {
        int userId = 1;
        Instructor instructor = new Instructor();
        instructor.setFee(0.0);

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.of(instructor));

        assertThatThrownBy(() -> payoutService.withdraw())
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INSUFFICIENT_BALANCE.getMessage());

        verify(instructorRepository).findById(userId);
        verify(payoutRepository, never()).save(any(Payout.class));
        verify(instructorRepository, never()).save(instructor);
        verify(emailService, never()).sendPayoutEmail(anyString(), anyString(), anyDouble(), anyString());
    }

    @Test
    public void testWithdrawInstructorNotFound() {
        int userId = 1;

        when(getAuthUserInfo.getAuthUserId()).thenReturn(userId);
        when(instructorRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> payoutService.withdraw())
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INSTRUCTOR_NOT_FOUND.getMessage());

        verify(instructorRepository).findById(userId);
        verify(payoutRepository, never()).save(any(Payout.class));
        verify(instructorRepository, never()).save(any(Instructor.class));
        verify(emailService, never()).sendPayoutEmail(anyString(), anyString(), anyDouble(), anyString());
    }
}
