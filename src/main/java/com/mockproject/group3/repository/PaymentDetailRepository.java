package com.mockproject.group3.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mockproject.group3.model.PaymentDetail;

public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, Integer> {
    Optional<PaymentDetail> findById(int paymentId);
    Optional<PaymentDetail> findByPaymentId(int paymentId);
}