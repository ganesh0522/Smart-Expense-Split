package com.expense.splitter.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long groupId;

    private Long paidBy;

    private Double amount;

    private String description;

    // EQUAL / CUSTOM / PERCENT
    private String splitType;

    private Long createdAt;
}