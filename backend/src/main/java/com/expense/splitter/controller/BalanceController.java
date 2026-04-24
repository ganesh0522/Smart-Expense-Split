package com.expense.splitter.controller;

import com.expense.splitter.model.Balance;
import com.expense.splitter.service.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/balances")
@RequiredArgsConstructor
public class BalanceController {

    // ✅ THIS WAS MISSING
    private final BalanceService balanceService;

    // ================= GET BY GROUP =================
    @GetMapping("/group/{groupId}")
    public List<Balance> getGroupBalances(@Valid @PathVariable Long groupId) {
        return balanceService.getGroupBalances(groupId);
    }
}