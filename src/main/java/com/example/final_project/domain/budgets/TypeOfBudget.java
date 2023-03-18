package com.example.final_project.domain.budgets;

import lombok.ToString;

import java.math.BigDecimal;

@ToString
public enum TypeOfBudget {
    HALF("can be exceeded to half of total value", BigDecimal.valueOf(1.5)),
    FULL("can be exceeded with no limit", BigDecimal.valueOf(-1)),
    STRICT("can't be exceeded", BigDecimal.valueOf(1));

    private final String title;
    private final BigDecimal value;

    TypeOfBudget(String title, BigDecimal value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getValue() {
        return value;
    }

}
