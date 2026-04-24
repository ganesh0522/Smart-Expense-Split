package com.expense.splitter.controller;

import com.expense.splitter.dto.SettlementDto;
import com.expense.splitter.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @GetMapping("/{groupId}")
    public List<SettlementDto> getSettlements(@PathVariable Long groupId) {
        return settlementService.simplify(groupId);
    }
}