package com.expense.splitter.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String test() {
        Object userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "User ID: " + userId;
    }
}