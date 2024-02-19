package com.example.final_project.domain.budgets;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

public record BudgetDetails(
        String title,
        BigDecimal limit,
        BudgetType budgetType,
        BigDecimal maxSingleExpense,
        TreeMap<Integer, LocalDateTime> historyOfChanges
) {
    @Builder
    public BudgetDetails {
    }

    public static BudgetDetails newOf(String title, BigDecimal limit,
                                      BudgetType budgetType,
                                      BigDecimal maxSingleExpense,
                                      TreeMap<Integer, LocalDateTime> historyOfChanges
    ) {
        return new BudgetDetails(title, limit, budgetType, maxSingleExpense, historyOfChanges);
    }
}
