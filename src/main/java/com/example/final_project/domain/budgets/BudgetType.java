package com.example.final_project.domain.budgets;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Getter
public enum BudgetType {
    HALF("Budget can be exceeded to half of total id", BigDecimal.valueOf(1.5)),
    FULL("Budget can be exceeded with no limit", BigDecimal.valueOf(-1)),
    STRICT("Budget can't be exceeded", BigDecimal.valueOf(1));

    private final String title;
    private final BigDecimal value;

    BudgetType(String title, BigDecimal value) {
        this.title = title;
        this.value = value;
    }

}
