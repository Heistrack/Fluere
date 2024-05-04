package com.example.final_project.budget.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

public record BudgetDetails(
        String title,
        BigDecimal limit,
        BudgetType budgetType,
        BigDecimal maxSingleExpense,
        MKTCurrency defaultCurrency,
        ExpenseSet expenseSet,
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
                                      MKTCurrency defaultCurrency,
                                      ExpenseSet expenseSet,
                                      TreeMap<Integer, LocalDateTime> historyOfChanges,
                                      BudgetPeriod budgetPeriod,
                                      String description
    ) {
        return new BudgetDetails(
                title, limit, budgetType, maxSingleExpense, defaultCurrency, expenseSet, historyOfChanges, budgetPeriod, description);
    }
}
