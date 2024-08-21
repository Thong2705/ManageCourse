package com.mockproject.group3.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mockproject.group3.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

public class PaymentSuccessControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentSuccessController paymentSuccessController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentSuccessController).build();
    }

    @Test
    public void testVnpayReturn() throws Exception {
        Map<String, String> allParams = new HashMap<>();
        allParams.put("vnp_TxnRef", "123456");
        allParams.put("vnp_ResponseCode", "00");

        mockMvc.perform(get("/payment/vnpay_return").param("vnp_TxnRef", "123456").param("vnp_ResponseCode", "00"))
                .andExpect(status().isOk())
                .andExpect(view().name("vnpay_success"));

        verify(paymentService, times(1)).handleVnpayReturn(anyMap());
    }
}
