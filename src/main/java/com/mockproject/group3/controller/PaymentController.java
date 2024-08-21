package com.mockproject.group3.controller;

import java.io.UnsupportedEncodingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mockproject.group3.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/create_payment/{course_id}")
    public ResponseEntity<?> paymentCourse(@PathVariable("course_id") int courseId) throws UnsupportedEncodingException{
        return paymentService.createPayment(courseId);

    }
}