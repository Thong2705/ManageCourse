package com.mockproject.group3.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.dto.request.subscription.SubscriptionReq;
import com.mockproject.group3.dto.response.BaseApiPaginationRespone;
import com.mockproject.group3.dto.response.BaseApiResponse;
import com.mockproject.group3.model.Subscription;
import com.mockproject.group3.service.SubscriptionService;

@SpringBootTest
public class SubscriptionControllerTest {
    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllSubscription() {
        PaginationParamReq req = new PaginationParamReq();
        Page<Subscription> page = new PageImpl<>(Collections.emptyList());
        when(subscriptionService.getAllSubcription(req)).thenReturn(page);

        ResponseEntity<BaseApiPaginationRespone<List<Subscription>>> response = subscriptionController
                .getAllSubcription(req);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPayload()).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testSubscription() {
        SubscriptionReq req = new SubscriptionReq();
        Subscription subscription = new Subscription();
        when(subscriptionService.subscription(req)).thenReturn(subscription);

        ResponseEntity<BaseApiResponse<Subscription>> response = subscriptionController.subscription(req);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPayload()).isEqualTo(subscription);
    }

    @Test
    public void testUnSubscription() {
        int subscriptionId = 1;

        ResponseEntity<BaseApiResponse<Void>> response = subscriptionController.unSubscription(subscriptionId);

        verify(subscriptionService).unSubscription(subscriptionId);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Unsubscription successfully");
    }
}
