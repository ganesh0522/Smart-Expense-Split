package com.expense.splitter.controller;

import com.expense.splitter.dto.ActivityResponse;
import com.expense.splitter.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    @GetMapping
    public List<ActivityResponse> getTimeline() {
        return timelineService.getTimeline();
    }
}