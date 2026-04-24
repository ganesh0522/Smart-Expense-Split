package com.expense.splitter.service;

import com.expense.splitter.dto.PaymentRequest;
import com.expense.splitter.model.Balance;
import com.expense.splitter.model.Payment;
import com.expense.splitter.repository.BalanceRepository;
import com.expense.splitter.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BalanceRepository balanceRepository;
    private final ActivityService activityService;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public void settle(PaymentRequest request) {

        Long fromUser = getCurrentUserId();
        Long toUser = request.getToUserId();
        double amount = request.getAmount();

        // ✅ VALIDATION
        if (amount <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }

        if (fromUser.equals(toUser)) {
            throw new RuntimeException("Cannot pay yourself");
        }

        // 1. Save payment
        paymentRepository.save(
                Payment.builder()
                        .fromUserId(fromUser)
                        .toUserId(toUser)
                        .amount(amount)
                        .createdAt(System.currentTimeMillis())
                        .build()
        );

        // 2. Adjust balances
        adjustBalance(fromUser, toUser, amount);

        // 3. Activity log
        activityService.log(
                fromUser,
                "SETTLE_PAYMENT",
                "Paid ₹" + amount + " to user " + toUser
        );
    }

    private void adjustBalance(Long fromUser, Long toUser, double amount) {

        Optional<Balance> forward = balanceRepository.findByUser1IdAndUser2Id(fromUser, toUser);
        Optional<Balance> reverse = balanceRepository.findByUser1IdAndUser2Id(toUser, fromUser);

        if (forward.isPresent()) {
            Balance b = forward.get();
            double newAmount = b.getAmount() - amount;

            if (newAmount <= 0) {
                balanceRepository.delete(b);

                if (newAmount < 0) {
                    createOrUpdateReverse(toUser, fromUser, -newAmount);
                }
            } else {
                b.setAmount(newAmount);
                balanceRepository.save(b);
            }

        } else if (reverse.isPresent()) {
            Balance b = reverse.get();
            b.setAmount(b.getAmount() + amount);
            balanceRepository.save(b);

        } else {
            createOrUpdateReverse(toUser, fromUser, amount);
        }
    }

    private void createOrUpdateReverse(Long user1, Long user2, double amount) {

        Balance balance = balanceRepository
                .findByUser1IdAndUser2Id(user1, user2)
                .orElse(
                        Balance.builder()
                                .user1Id(user1)
                                .user2Id(user2)
                                .amount(0.0)
                                .build()
                );

        balance.setAmount(balance.getAmount() + amount);

        balanceRepository.save(balance);
    }
}