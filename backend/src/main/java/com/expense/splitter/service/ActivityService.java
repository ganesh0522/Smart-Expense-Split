package com.expense.splitter.service;

import com.expense.splitter.dto.ActivityResponse;
import com.expense.splitter.model.Activity;
import com.expense.splitter.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    // 🔹 SAVE ACTIVITY
    public void log(Long userId, String action, String message) {

        activityRepository.save(
                Activity.builder()
                        .userId(userId)
                        .action(action)
                        .message(message)
                        .createdAt(System.currentTimeMillis())
                        .build()
        );
    }

    // 🔹 FETCH ALL
    public List<ActivityResponse> getAll() {

        return activityRepository.findAll()
                .stream()
                .sorted(Comparator.comparingLong(Activity::getCreatedAt).reversed())
                .map(a -> new ActivityResponse(
                        a.getUserId(),
                        a.getAction(),
                        a.getMessage(),
                        a.getCreatedAt()
                ))
                .toList();
    }
}