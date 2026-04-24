package com.expense.splitter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateExpenseRequest {

    @NotNull
    private Long groupId;

    @Min(1)
    private double amount;

    @NotBlank
    private String description;

    @NotBlank
    private String splitType;

    private Long paidBy;

    private List<SplitRequest> splits;
}