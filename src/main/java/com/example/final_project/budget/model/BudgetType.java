package com.example.final_project.budget.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
@AllArgsConstructor
public enum BudgetType {
    HALF("Budget can be exceeded by half of total limit", BigDecimal.valueOf(1.5)),
    FULL("Budget can be exceeded with no limit", BigDecimal.valueOf(-1)),
    STRICT("Budget can't be exceeded", BigDecimal.valueOf(1));

    private final String title;
    private final BigDecimal value;
}
