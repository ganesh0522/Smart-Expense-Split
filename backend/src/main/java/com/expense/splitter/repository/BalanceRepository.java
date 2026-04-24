package com.expense.splitter.repository;

import com.expense.splitter.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);
    List<Balance> findAll();
    List<Balance> findByGroupId(Long groupId);
    Optional<Balance> findByUser1IdAndUser2IdAndGroupId(
            Long user1Id,
            Long user2Id,
            Long groupId
    );
}