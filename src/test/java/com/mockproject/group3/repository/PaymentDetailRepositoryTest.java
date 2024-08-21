package com.mockproject.group3.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mockproject.group3.model.PaymentDetail;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentDetailRepositoryTest {

    @Mock
    private PaymentDetailRepository paymentDetailRepository;

    @Test
    void testFindById_PaymentDetailExists() {
        PaymentDetail paymentDetail = new PaymentDetail();
        paymentDetail.setId(1);
        
        when(paymentDetailRepository.findById(1)).thenReturn(Optional.of(paymentDetail));

        Optional<PaymentDetail> result = paymentDetailRepository.findById(1);
        
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void testFindById_PaymentDetailDoesNotExist() {
        when(paymentDetailRepository.findById(1)).thenReturn(Optional.empty());

        Optional<PaymentDetail> result = paymentDetailRepository.findById(1);

        assertFalse(result.isPresent());
    }
}
