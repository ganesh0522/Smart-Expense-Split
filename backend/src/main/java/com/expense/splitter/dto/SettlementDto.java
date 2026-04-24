package com.expense.splitter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SettlementDto {
    private Long fromUserId;
    private Long toUserId;
    private double amount;
}