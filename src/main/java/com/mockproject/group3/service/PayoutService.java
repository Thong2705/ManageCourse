package com.mockproject.group3.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.mockproject.group3.enums.Template;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Instructor;
import com.mockproject.group3.model.Payout;
import com.mockproject.group3.repository.InstructorRepository;
import com.mockproject.group3.repository.PayoutRepository;
import com.mockproject.group3.utils.GetAuthUserInfo;

@Service
public class PayoutService {
    
    private PayoutRepository payoutRepository;
    private InstructorRepository instructorRepository;
    private EmailService emailService;
    private GetAuthUserInfo getAuthUserInfo;

    public PayoutService(PayoutRepository payoutRepository, InstructorRepository instructorRepository,
            GetAuthUserInfo getAuthUserInfo, EmailService emailService) {
        this.payoutRepository = payoutRepository;
        this.instructorRepository = instructorRepository;
        this.getAuthUserInfo = getAuthUserInfo;
        this.emailService = emailService;
    }

    // @PreAuthorize("hasRole('INSTRUCTOR')")
    public void withdraw() {
        int userId = getAuthUserInfo.getAuthUserId();
        Instructor instructor = instructorRepository.findById(1)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));
        if (instructor.getFee() == 0)
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);

        double fee = instructor.getFee();

        Payout payout = new Payout();
        payout.setAmount(instructor.getFee());
        payout.setCreated_at(LocalDateTime.now());
        payout.setInstructor(instructor);

        payoutRepository.save(payout);

        instructor.setFee(0);
        instructorRepository.save(instructor);

        emailService.sendPayoutEmail(instructor.getUser().getEmail(), "Withdraw Successfully", fee,
                Template.PAYOUT_TEMPLATE.getTemplateName());
    }
}
