package com.expense.splitter.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "expense_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // creator user id
    private Long createdBy;

    private Long createdAt;
}