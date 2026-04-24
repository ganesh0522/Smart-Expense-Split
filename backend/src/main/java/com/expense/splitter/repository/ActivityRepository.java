package com.expense.splitter.repository;

import com.expense.splitter.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // ✅ ADD THIS
    List<Activity> findAllByOrderByCreatedAtDesc();
}