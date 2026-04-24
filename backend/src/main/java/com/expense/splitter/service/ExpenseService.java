package com.expense.splitter.service;

import com.expense.splitter.dto.CreateExpenseRequest;
import com.expense.splitter.dto.SplitRequest;
import com.expense.splitter.model.Balance;
import com.expense.splitter.model.Expense;
import com.expense.splitter.model.ExpenseSplit;
import com.expense.splitter.repository.BalanceRepository;
import com.expense.splitter.repository.ExpenseRepository;
import com.expense.splitter.repository.ExpenseSplitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository splitRepository;
    private final BalanceRepository balanceRepository;
    private final ActivityService activityService;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // ===================== MAIN =====================

    @Transactional
    public void addExpense(CreateExpenseRequest request) {

        if (request.getAmount() <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }

        if (request.getSplits() == null || request.getSplits().isEmpty()) {
            throw new RuntimeException("Splits cannot be empty");
        }

        Long paidBy = request.getPaidBy();

        if (paidBy == null) {
            paidBy = getCurrentUserId(); // fallback
        }

        Expense expense = Expense.builder()
                .groupId(request.getGroupId())
                .paidBy(paidBy)
                .amount(round(request.getAmount()))
                .description(request.getDescription())
                .splitType(request.getSplitType())
                .createdAt(System.currentTimeMillis())
                .build();

        expense = expenseRepository.save(expense);

        activityService.log(
                paidBy,
                "ADD_EXPENSE",
                "Added expense: " + expense.getDescription() + " ₹" + expense.getAmount()
        );

        List<SplitRequest> splits = request.getSplits();

        switch (request.getSplitType().toUpperCase()) {
            case "EQUAL":
                handleEqualSplit(expense, splits);
                break;
            case "CUSTOM":
                handleCustomSplit(expense, splits);
                break;
            case "PERCENT":
                handlePercentSplit(expense, splits);
                break;
            default:
                throw new RuntimeException("Invalid split type");
        }
    }

    // ===================== SPLIT TYPES =====================

    private void handleEqualSplit(Expense expense, List<SplitRequest> splits) {

        double share = round(expense.getAmount() / splits.size());

        for (SplitRequest s : splits) {
            saveAndUpdate(expense, s.getUserId(), share);
        }
    }

    private void handleCustomSplit(Expense expense, List<SplitRequest> splits) {

        double total = splits.stream()
                .mapToDouble(SplitRequest::getAmount)
                .sum();

        if (round(total) != round(expense.getAmount())) {
            throw new RuntimeException("Custom split total must equal expense amount");
        }

        for (SplitRequest s : splits) {
            saveAndUpdate(expense, s.getUserId(), round(s.getAmount()));
        }
    }

    private void handlePercentSplit(Expense expense, List<SplitRequest> splits) {

        double totalPercent = splits.stream()
                .mapToDouble(SplitRequest::getPercent)
                .sum();

        if (round(totalPercent) != 100.0) {
            throw new RuntimeException("Total percentage must be 100");
        }

        for (SplitRequest s : splits) {
            double amount = round((expense.getAmount() * s.getPercent()) / 100);
            saveAndUpdate(expense, s.getUserId(), amount);
        }
    }

    // ===================== CORE LOGIC =====================

    private void saveAndUpdate(Expense expense, Long userId, double amount) {

        // Save split
        splitRepository.save(ExpenseSplit.builder()
                .expenseId(expense.getId())
                .userId(userId)
                .amount(amount)
                .build());

        // Skip self
        if (expense.getPaidBy().equals(userId)) return;

        updateBalance(
                expense.getPaidBy(),
                userId,
                amount,
                expense.getGroupId()   // ✅ FIXED
        );
    }

    /**
     * ✅ FINAL NET BALANCE LOGIC (VERY IMPORTANT)
     *
     * Handles:
     * - Forward balance (A owes B)
     * - Reverse balance (B owes A)
     * - Cancels out correctly
     * - Prevents duplicates
     */
    private void updateBalance(Long payer, Long user, double amount, Long groupId) {

        if (payer.equals(user)) return;

        Optional<Balance> forward =
                balanceRepository.findByUser1IdAndUser2IdAndGroupId(user, payer, groupId);

        Optional<Balance> reverse =
                balanceRepository.findByUser1IdAndUser2IdAndGroupId(payer, user, groupId);

        if (forward.isPresent()) {
            // user owes payer → increase
            Balance b = forward.get();
            b.setAmount(round(b.getAmount() + amount));
            balanceRepository.save(b);

        } else if (reverse.isPresent()) {
            // payer owes user → cancel
            Balance b = reverse.get();
            double newAmount = round(b.getAmount() - amount);

            if (newAmount > 0) {
                b.setAmount(newAmount);
                balanceRepository.save(b);
            } else if (newAmount < 0) {
                // reverse direction
                balanceRepository.delete(b);

                balanceRepository.save(Balance.builder()
                        .groupId(groupId)
                        .user1Id(user)
                        .user2Id(payer)
                        .amount(round(-newAmount))
                        .build());
            } else {
                // zero → remove
                balanceRepository.delete(b);
            }

        } else {
            // no relation → create new
            balanceRepository.save(Balance.builder()
                    .groupId(groupId)
                    .user1Id(user)
                    .user2Id(payer)
                    .amount(round(amount))
                    .build());
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // ===================== FETCH =====================

    public List<Expense> getGroupExpenses(Long groupId) {
        return expenseRepository.findByGroupId(groupId);
    }
}