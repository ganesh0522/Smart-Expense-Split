package com.expense.splitter.controller;

import com.expense.splitter.dto.ActivityResponse;
import com.expense.splitter.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public List<ActivityResponse> getActivities() {
        return activityService.getAll();
    }
}