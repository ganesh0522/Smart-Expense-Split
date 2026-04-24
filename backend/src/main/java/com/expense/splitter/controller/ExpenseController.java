package com.expense.splitter.controller;

import com.expense.splitter.dto.CreateExpenseRequest;
import com.expense.splitter.model.Expense;
import com.expense.splitter.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public String addExpense(@Valid @RequestBody CreateExpenseRequest request) {
        expenseService.addExpense(request);
        return "Expense added successfully";
    }

    @GetMapping("/group/{groupId}")
    public List<Expense> getGroupExpenses(@PathVariable Long groupId) {
        return expenseService.getGroupExpenses(groupId);
    }
}