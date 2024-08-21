package com.mockproject.group3.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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

@Service
public class SubscriptionService {
    private SubscriptionRepository subscriptionRepository;
    private InstructorRepository instructorRepository;
    private StudentRepository studentRepository;
    private GetAuthUserInfo getAuthUserInfo;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
            InstructorRepository instructorRepository, StudentRepository studentRepository,
            GetAuthUserInfo getAuthUserInfo) {
        this.subscriptionRepository = subscriptionRepository;
        this.instructorRepository = instructorRepository;
        this.studentRepository = studentRepository;
        this.getAuthUserInfo = getAuthUserInfo;
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    public Page<Subscription> getAllSubcription(PaginationParamReq req) {
        PageRequest pageRequest = PageRequest.of(req.getPage() - 1 >= 0 ? req.getPage() - 1 : 0, req.getPageSize());
        Sort sort = Sort.by("id").descending();

        return subscriptionRepository.findAll(pageRequest.withSort(sort));
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    public Subscription subscription(SubscriptionReq req) {
        Instructor instructor = instructorRepository.findById(req.getIdInstructor())
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND));
        int studentId = getAuthUserInfo.getAuthUserId();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        Subscription subscription = new Subscription();
        subscription.setInstructor(instructor);
        subscription.setStudent(student);
        return subscriptionRepository.save(subscription);

    }

    @PreAuthorize("hasAuthority('STUDENT')")
    public void unSubscription(int id) {
        int studentId = getAuthUserInfo.getAuthUserId();
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
        if (subscription.getStudent().getId() != studentId)
            throw new AppException(ErrorCode.ACTION_NOT_ALLOW);

        subscriptionRepository.delete(subscription);
    }
}
