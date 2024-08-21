package com.mockproject.group3.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.dto.request.subscription.SubscriptionReq;
import com.mockproject.group3.dto.response.BaseApiPaginationRespone;
import com.mockproject.group3.dto.response.BaseApiResponse;
import com.mockproject.group3.model.Subscription;
import com.mockproject.group3.service.SubscriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/subscription")
class SubscriptionController {
    private SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping()
    public ResponseEntity<BaseApiPaginationRespone<List<Subscription>>> getAllSubcription(
            @Valid @ModelAttribute PaginationParamReq req) {
        Page<Subscription> result = subscriptionService.getAllSubcription(req);

        return ResponseEntity.ok()
                .body(new BaseApiPaginationRespone<List<Subscription>>(0, "Get list subscription succesfully",
                        result.toList(),
                        result.getNumber() + 1, result.getSize(), result.getTotalPages(),
                        result.getNumberOfElements()));
    }

    @PostMapping()
    public ResponseEntity<BaseApiResponse<Subscription>> subscription(
            @Valid @RequestBody SubscriptionReq req) {
        return ResponseEntity.ok()
                .body(new BaseApiResponse<Subscription>(0, "Subscription succesfully",
                        subscriptionService.subscription(req)));
    }

    @PostMapping("/{id}")
    public ResponseEntity<BaseApiResponse<Void>> unSubscription(@PathVariable int id) {
        subscriptionService.unSubscription(id);

        return ResponseEntity.ok()
                .body(new BaseApiResponse<>(0, "Unsubscription successfully"));
    }

}