package com.expense.splitter.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResponse {

    private double totalYouOwe;
    private double totalYouGet;
    private double netBalance;

    private List<UserBalanceDto> youOweList;
    private List<UserBalanceDto> youGetList;
}