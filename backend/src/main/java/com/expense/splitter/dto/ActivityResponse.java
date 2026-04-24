package com.expense.splitter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivityResponse {

    private Long userId;
    private String action;
    private String message; // ✅ NOT details
    private Long timestamp;
}