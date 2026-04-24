package com.expense.splitter.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String action; // CREATE_GROUP, ADD_EXPENSE, SETTLE

    private String message;

    private Long createdAt;
}