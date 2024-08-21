package com.mockproject.group3.controller;

import java.util.Map;

import org.slf4j.Logger; // Add this import statement
import org.slf4j.LoggerFactory; // Add this import statement
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; // Add this import statement
import org.springframework.web.bind.annotation.RequestParam;

import com.mockproject.group3.service.PaymentService;

@Controller
@RequestMapping("/payment")
public class PaymentSuccessController {

    private final PaymentService paymentService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    public PaymentSuccessController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/vnpay_return")
    public String vnpayReturn(@RequestParam Map<String, String> allParams) {
        logger.info("Received VNPay callback with params: {}", allParams);
        paymentService.handleVnpayReturn(allParams);
        return "vnpay_success";
    }

}