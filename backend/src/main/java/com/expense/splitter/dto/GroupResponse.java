package com.expense.splitter.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GroupResponse {
    private Long id;
    private String name;
    private Long createdBy;
    private List<MemberDto> members;
}