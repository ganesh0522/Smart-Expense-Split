package com.expense.splitter.controller;

import com.expense.splitter.dto.CreateGroupRequest;
import com.expense.splitter.dto.GroupResponse;
import com.expense.splitter.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public GroupResponse create(@Valid @RequestBody CreateGroupRequest request) {
        return groupService.createGroup(request);
    }

    @GetMapping
    public List<GroupResponse> getMyGroups() {
        return groupService.getUserGroups();
    }

    @GetMapping("/{id}")
    public GroupResponse getGroup(@PathVariable Long id) {
        return groupService.getGroup(id);
    }
}