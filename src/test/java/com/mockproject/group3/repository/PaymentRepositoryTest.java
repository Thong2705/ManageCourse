package com.mockproject.group3.repository;

import com.mockproject.group3.enums.Status;
import com.mockproject.group3.model.Payment;
import com.mockproject.group3.model.Student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class PaymentRepositoryTest {

    @Mock
    private PaymentRepository paymentRepository;

    private Payment payment;

    @BeforeEach
    void setUp() {

        Student student = new Student();
        student.setId(1);
        payment = new Payment();
        payment.setTxnRef("12345678");
        payment.setStatus(Status.PENDING);
        payment.setStudent(student);
        paymentRepository.save(payment);
    }

    @Test
    void testFindByTxnRef() {
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentRepository.findByTxnRef("12345678")).thenReturn(payment);

        assertNotNull(payment);
        assertEquals("12345678", payment.getTxnRef());
    }

    @Test
    public void testFindByStudentIdAndStatus() {
        List<Payment> paymentsList = new ArrayList<>();
        paymentsList.add(payment);

        when(paymentRepository.findByStudentIdAndStatus(1, Status.PENDING)).thenReturn(paymentsList);

        List<Payment> payments = paymentRepository.findByStudentIdAndStatus(1, Status.PENDING);
        
        assertNotNull(payments);
        assertEquals(1, payments.size()); // Chắc chắn rằng danh sách không trống trước khi truy cập vào phần tử đầu tiên
        assertEquals(Status.PENDING, payments.get(0).getStatus());
    }

}
