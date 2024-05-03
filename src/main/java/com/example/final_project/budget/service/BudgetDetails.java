package com.example.final_project.budget.service;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

public record BudgetDetails(
        String title,
        BigDecimal limit,
        BudgetType budgetType,
        BigDecimal maxSingleExpense,
        TreeMap<Integer, LocalDateTime> historyOfChanges,
        BudgetPeriod budgetPeriod,
        String description
) {
    @Builder
    public BudgetDetails {
    }

    public static BudgetDetails newOf(String title, BigDecimal limit,
                                      BudgetType budgetType,
                                      BigDecimal maxSingleExpense,
                                      TreeMap<Integer, LocalDateTime> historyOfChanges,
                                      BudgetPeriod budgetPeriod,
                                      String description
    ) {
        return new BudgetDetails(title, limit, budgetType, maxSingleExpense, historyOfChanges, budgetPeriod, description);
    }
}
