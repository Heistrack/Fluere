package com.example.final_project.api.responses;

import java.math.BigDecimal;

public record BudgetStatusDTO(
        String budgetId,
        Integer totalExpensesNumber,
        BigDecimal amountNow,
        BigDecimal amountLeft,
        BigDecimal budgetFullfillPerc,
        String typeOfBudget,
        String maxValue
) {
    public static BudgetStatusDTO newOf(String budgetId,
                                        Integer totalExpensesNumber,
                                        BigDecimal amountNow,
                                        BigDecimal amountLeft,
                                        BigDecimal budgetFullfillPerc,
                                        String typeOfBudget,
                                        String maxValue) {
        return new BudgetStatusDTO(
                budgetId,
                totalExpensesNumber,
                amountNow,
                amountLeft,
                budgetFullfillPerc,
                typeOfBudget,
                maxValue);
    }
}
