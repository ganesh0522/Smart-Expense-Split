package com.expense.splitter.service;

import com.expense.splitter.dto.ActivityResponse;
import com.expense.splitter.model.Activity;
import com.expense.splitter.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final ActivityRepository activityRepository;

    public List<ActivityResponse> getTimeline() {

        List<Activity> activities =
                activityRepository.findAllByOrderByCreatedAtDesc();

        return activities.stream()
                .map(a -> new ActivityResponse(
                        a.getUserId(),
                        a.getAction(),
                        a.getMessage(), // ✅ FIXED (not getDetails)
                        a.getCreatedAt()
                ))
                .toList();
    }
}