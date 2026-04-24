package com.expense.splitter.service;

import com.expense.splitter.dto.DashboardResponse;
import com.expense.splitter.dto.UserBalanceDto;
import com.expense.splitter.model.Balance;
import com.expense.splitter.model.User;
import com.expense.splitter.repository.BalanceRepository;
import com.expense.splitter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BalanceRepository balanceRepository;
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public DashboardResponse getDashboard() {

        Long userId = getCurrentUserId();

        List<Balance> all = balanceRepository.findAll();

        double totalOwe = 0;
        double totalGet = 0;

        List<UserBalanceDto> oweList = new ArrayList<>();
        List<UserBalanceDto> getList = new ArrayList<>();

        for (Balance b : all) {

            if (b.getUser1Id().equals(userId)) {
                // you owe
                User user = userRepository.findById(b.getUser2Id()).orElseThrow();
                totalOwe += b.getAmount();

                oweList.add(new UserBalanceDto(
                        user.getId(),
                        user.getName(),
                        b.getAmount()
                ));
            }

            else if (b.getUser2Id().equals(userId)) {
                // you get
                User user = userRepository.findById(b.getUser1Id()).orElseThrow();
                totalGet += b.getAmount();

                getList.add(new UserBalanceDto(
                        user.getId(),
                        user.getName(),
                        b.getAmount()
                ));
            }
        }

        return DashboardResponse.builder()
                .totalYouOwe(totalOwe)
                .totalYouGet(totalGet)
                .netBalance(totalGet - totalOwe)
                .youOweList(oweList)
                .youGetList(getList)
                .build();
    }
}