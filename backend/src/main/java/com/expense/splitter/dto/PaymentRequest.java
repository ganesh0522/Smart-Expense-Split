package com.expense.splitter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull
    private Long toUserId;

    @Min(value = 1, message = "Amount must be greater than 0")
    private double amount;
}