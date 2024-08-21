package com.mockproject.group3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mockproject.group3.config.Config;
import com.mockproject.group3.dto.PaymentDTO;
import com.mockproject.group3.enums.Status;
import com.mockproject.group3.exception.AppException;
import com.mockproject.group3.exception.ErrorCode;
import com.mockproject.group3.model.Course;
import com.mockproject.group3.model.Instructor;
import com.mockproject.group3.model.Payment;
import com.mockproject.group3.model.PaymentDetail;
import com.mockproject.group3.model.Student;
import com.mockproject.group3.model.Users;
import com.mockproject.group3.repository.CourseRepository;
import com.mockproject.group3.repository.InstructorRepository;
import com.mockproject.group3.repository.PaymentDetailRepository;
import com.mockproject.group3.repository.PaymentRepository;
import com.mockproject.group3.repository.StudentRepository;
import com.mockproject.group3.utils.GetAuthUserInfo;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private PaymentDetailRepository paymentDetailRepository;

    @Mock
    private GetAuthUserInfo getAuthUserInfo;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Course course;
    private Student student;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1);
        course.setPrice(1000.0);

        student = new Student();
        student.setId(1);
    }

    @Test
    void testCreatePayment_Success() throws UnsupportedEncodingException {
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.of(student));

        ResponseEntity<?> response = paymentService.createPayment(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof PaymentDTO);

        PaymentDTO paymentDTO = (PaymentDTO) response.getBody();
        assertEquals("Ok", paymentDTO.getStatus());
        assertEquals("Success", paymentDTO.getMessage());
        assertNotNull(paymentDTO.getUrl());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(paymentDetailRepository, times(1)).save(any(PaymentDetail.class));
    }

    @Test
    void testCreatePayment_CourseNotFound() {
        when(courseRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            paymentService.createPayment(1);
        });

        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testCreatePayment_StudentNotFound() throws UnsupportedEncodingException {
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(getAuthUserInfo.getAuthUserId()).thenReturn(1);
        when(studentRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> {
            paymentService.createPayment(1);
        });

        assertEquals(ErrorCode.STUDENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testHandleVnpayReturn_Success() {
        String vnp_TxnRef = "txn123";
        String vnp_ResponseCode = "00";
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", vnp_TxnRef);
        params.put("vnp_ResponseCode", vnp_ResponseCode);

        Payment payment = new Payment();
        payment.setTxnRef(vnp_TxnRef);
        payment.setStatus(Status.PENDING); // Set initial status if necessary
        payment.setAmount(10000); // Set payment amount if necessary
        payment.setId(1); // Set payment ID if necessary

        PaymentDetail paymentDetail = new PaymentDetail();
        Course course = new Course();
        Instructor instructor = new Instructor();
        instructor.setFee(5000);
        course.setInstructor(instructor);
        paymentDetail.setCourses(course);
        
        when(paymentRepository.findByTxnRef(vnp_TxnRef)).thenReturn(payment);
        when(paymentDetailRepository.findByPaymentId(payment.getId())).thenReturn(Optional.of(paymentDetail));

        ResponseEntity<?> response = paymentService.handleVnpayReturn(params);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Payment completed successfully", response.getBody());

        verify(paymentRepository, times(1)).findByTxnRef(vnp_TxnRef);
        verify(paymentRepository, times(1)).save(payment);
        verify(paymentDetailRepository, times(1)).findByPaymentId(payment.getId());
        verify(instructorRepository, times(1)).save(instructor);

        assertEquals(Status.APPROVED, payment.getStatus());
        assertEquals(15000, instructor.getFee()); // Assuming the payment amount is 10000
    }


    @Test
    void testHandleVnpayReturn_PaymentNotFound() {
        String vnp_TxnRef = "txn123";
        String vnp_ResponseCode = "00";
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", vnp_TxnRef);
        params.put("vnp_ResponseCode", vnp_ResponseCode);

        when(paymentRepository.findByTxnRef(vnp_TxnRef)).thenReturn(null);

        ResponseEntity<?> response = paymentService.handleVnpayReturn(params);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ErrorCode.PAYMENT_NOT_FOUND, response.getBody());

        verify(paymentRepository, times(1)).findByTxnRef(vnp_TxnRef);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testHandleVnpayReturn_Failed() {
        String vnp_TxnRef = "txn123";
        String vnp_ResponseCode = "99";
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", vnp_TxnRef);
        params.put("vnp_ResponseCode", vnp_ResponseCode);

        ResponseEntity<?> response = paymentService.handleVnpayReturn(params);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorCode.PAYMENT_FAILED, response.getBody());

        verify(paymentRepository, never()).findByTxnRef(anyString());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    


}