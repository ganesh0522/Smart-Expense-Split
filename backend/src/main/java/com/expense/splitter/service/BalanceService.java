package com.expense.splitter.service;

import com.expense.splitter.model.Balance;
import com.expense.splitter.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;

    public List<Balance> getGroupBalances(Long groupId) {

        if (groupId == null) {
            throw new RuntimeException("Group ID is required");
        }

        return balanceRepository.findByGroupId(groupId);
    }
}
