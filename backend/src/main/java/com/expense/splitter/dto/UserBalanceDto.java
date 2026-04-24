package com.expense.splitter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBalanceDto {
    private Long userId;
    private String name;
    private Double amount;
}