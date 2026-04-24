package com.expense.splitter.dto;

import lombok.Data;

@Data
public class SplitRequest {
    private Long userId;
    private Double amount;   // used for CUSTOM
    private Double percent;  // used for PERCENT
}