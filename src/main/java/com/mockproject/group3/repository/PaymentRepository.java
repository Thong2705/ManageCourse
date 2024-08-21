package com.mockproject.group3.repository;

import com.mockproject.group3.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mockproject.group3.model.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findByTxnRef(String txnRef);
    List<Payment> findByStudentIdAndStatus(Integer studentId, Status status);
}
