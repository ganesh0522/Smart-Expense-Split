package com.expense.splitter.controller;

import com.expense.splitter.dto.PaymentRequest;
import com.expense.splitter.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/settle")
    public String settle(@Valid @RequestBody PaymentRequest request) {
        paymentService.settle(request);
        return "Payment successful";
    }
}