package com.mockproject.group3.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mockproject.group3.dto.response.BaseApiResponse;
import com.mockproject.group3.service.PayoutService;

@RestController
@RequestMapping(path = "/api/payout")
public class PayoutController {
    private PayoutService payoutService;

    public PayoutController(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

    @PostMapping()
    public ResponseEntity<BaseApiResponse<Void>> withdraw() {
        payoutService.withdraw();
        return ResponseEntity.ok().body(new BaseApiResponse<>(0, "withdraw successfully"));
    }

}
