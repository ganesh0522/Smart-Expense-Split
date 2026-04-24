package com.expense.splitter.service;

import com.expense.splitter.dto.SettlementDto;
import com.expense.splitter.model.Balance;
import com.expense.splitter.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final BalanceRepository balanceRepository;

    // ✅ IMPORTANT: pass groupId
    public List<SettlementDto> simplify(Long groupId) {

        // ✅ fetch only group balances
        List<Balance> balances = balanceRepository.findByGroupId(groupId);

        Map<Long, Double> net = new HashMap<>();

        // ===================== STEP 1: NET BALANCE =====================
        for (Balance b : balances) {

            if (b.getUser1Id().equals(b.getUser2Id())) continue;

            net.put(
                    b.getUser1Id(),
                    net.getOrDefault(b.getUser1Id(), 0.0) - b.getAmount()
            );

            net.put(
                    b.getUser2Id(),
                    net.getOrDefault(b.getUser2Id(), 0.0) + b.getAmount()
            );
        }

        // ===================== STEP 2: SPLIT USERS =====================
        List<UserBalance> debtors = new ArrayList<>();
        List<UserBalance> creditors = new ArrayList<>();

        for (Map.Entry<Long, Double> entry : net.entrySet()) {

            double amount = round(entry.getValue());

            if (amount < 0) {
                debtors.add(new UserBalance(entry.getKey(), -amount));
            } else if (amount > 0) {
                creditors.add(new UserBalance(entry.getKey(), amount));
            }
        }

        // ===================== STEP 3: SORT =====================
        debtors.sort(Comparator.comparingDouble(a -> a.amount));          // smallest debt first
        creditors.sort((a, b) -> Double.compare(b.amount, a.amount));     // largest credit first

        // ===================== STEP 4: MINIMIZE TRANSACTIONS =====================
        List<SettlementDto> result = new ArrayList<>();

        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {

            UserBalance d = debtors.get(i);
            UserBalance c = creditors.get(j);

            double settled = Math.min(d.amount, c.amount);

            result.add(new SettlementDto(
                    d.userId,
                    c.userId,
                    round(settled)
            ));

            d.amount -= settled;
            c.amount -= settled;

            if (d.amount == 0) i++;
            if (c.amount == 0) j++;
        }

        return result;
    }

    // ===================== HELPER CLASS =====================
    private static class UserBalance {
        Long userId;
        double amount;

        UserBalance(Long userId, double amount) {
            this.userId = userId;
            this.amount = amount;
        }
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}