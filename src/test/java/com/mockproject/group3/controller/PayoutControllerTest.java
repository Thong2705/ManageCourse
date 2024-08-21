package com.mockproject.group3.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.mockproject.group3.dto.response.BaseApiResponse;
import com.mockproject.group3.service.PayoutService;

public class PayoutControllerTest {
    @Mock
    private PayoutService payoutService;

    @InjectMocks
    private PayoutController payoutController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testWithdraw() {
        ResponseEntity<BaseApiResponse<Void>> response = payoutController.withdraw();

        verify(payoutService).withdraw();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("withdraw successfully");
        assertThat(response.getBody().getCode()).isEqualTo(0);
    }
}
