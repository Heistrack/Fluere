package com.example.final_project.api.responses.budgets;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.UUID;

public record BudgetStatusDTO(
        UUID budgetId,
        Integer totalExpensesNumber,
        BigDecimal amountNow,
        BigDecimal amountLeft,
        BigDecimal budgetFullFillPercent,
        String typeOfBudget,
        String limit,
        BigDecimal maxSingleExpense,
        TreeMap<Integer,LocalDateTime> historyOfChanges
) {
    public static BudgetStatusDTO newOf(UUID budgetId,
                                        Integer totalExpensesNumber,
                                        BigDecimal amountNow,
                                        BigDecimal amountLeft,
                                        BigDecimal budgetFullFillPercent,
                                        String typeOfBudget,
                                        String limit,
                                        BigDecimal maxSingleExpense,
                                        TreeMap<Integer,LocalDateTime> timestamp
    ) {
        return new BudgetStatusDTO(
                budgetId,
                totalExpensesNumber,
                amountNow,
                amountLeft,
                budgetFullFillPercent,
                typeOfBudget,
                limit,
                maxSingleExpense,
                timestamp
        );
    }
}
