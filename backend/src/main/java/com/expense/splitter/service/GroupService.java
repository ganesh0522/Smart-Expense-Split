package com.expense.splitter.service;

import com.expense.splitter.dto.CreateGroupRequest;
import com.expense.splitter.dto.GroupResponse;
import com.expense.splitter.dto.MemberDto;
import com.expense.splitter.model.Group;
import com.expense.splitter.model.GroupMember;
import com.expense.splitter.model.User;
import com.expense.splitter.repository.GroupMemberRepository;
import com.expense.splitter.repository.GroupRepository;
import com.expense.splitter.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final ActivityService activityService;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // ===================== CREATE GROUP =====================

    public GroupResponse createGroup(CreateGroupRequest request) {

        Long creatorId = getCurrentUserId();

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Group name is required");
        }

        // ✅ Remove duplicates
        Set<Long> uniqueMembers = new HashSet<>();

        if (request.getMemberIds() != null) {
            uniqueMembers.addAll(request.getMemberIds());
        }

        uniqueMembers.add(creatorId); // always include creator

        Group group = groupRepository.save(
                Group.builder()
                        .name(request.getName().trim())
                        .createdBy(creatorId)
                        .createdAt(System.currentTimeMillis())
                        .build()
        );

        for (Long userId : uniqueMembers) {
            memberRepository.save(
                    GroupMember.builder()
                            .groupId(group.getId())
                            .userId(userId)
                            .build()
            );
        }

        return buildResponse(group);
    }

    // ===================== GET USER GROUPS =====================

    public List<GroupResponse> getUserGroups() {

        Long userId = getCurrentUserId();

        return memberRepository.findByUserId(userId)
                .stream()
                .map(m -> groupRepository.findById(m.getGroupId()).orElseThrow())
                .distinct()
                .map(this::buildResponse)
                .toList();
    }

    // ===================== GET SINGLE GROUP =====================

    public GroupResponse getGroup(Long groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        return buildResponse(group);
    }

    // ===================== BUILD RESPONSE =====================

    private GroupResponse buildResponse(Group group) {

        Set<Long> uniqueUserIds = memberRepository.findByGroupId(group.getId())
                .stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toSet());

        List<MemberDto> members = userRepository.findAllById(uniqueUserIds)
                .stream()
                .map(u -> new MemberDto(u.getId(), u.getName()))
                .toList();

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .createdBy(group.getCreatedBy())
                .members(members)
                .build();
    }
}