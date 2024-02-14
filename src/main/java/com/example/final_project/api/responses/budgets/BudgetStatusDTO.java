package com.example.final_project.api.responses.budgets;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BudgetStatusDTO(
        String budgetId,
        Integer totalExpensesNumber,
        BigDecimal amountNow,
        BigDecimal amountLeft,
        BigDecimal budgetFullFillPercent,
        String typeOfBudget,
        BigDecimal limit,
        BigDecimal maxSingleExpense,
        LocalDateTime timestamp
) {
    public static BudgetStatusDTO newOf(String budgetId,
                                        Integer totalExpensesNumber,
                                        BigDecimal amountNow,
                                        BigDecimal amountLeft,
                                        BigDecimal budgetFullFillPercent,
                                        String typeOfBudget,
                                        BigDecimal limit,
                                        BigDecimal maxSingleExpense,
                                        LocalDateTime timestamp) {
        return new BudgetStatusDTO(
                budgetId,
                totalExpensesNumber,
                amountNow,
                amountLeft,
                budgetFullFillPercent,
                typeOfBudget,
                limit,
                maxSingleExpense,
                timestamp);
    }
}
