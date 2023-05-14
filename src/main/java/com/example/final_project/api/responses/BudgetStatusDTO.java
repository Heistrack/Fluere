package com.example.final_project.api.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BudgetStatusDTO(
        String budgetId,
        Integer totalExpensesNumber,
        BigDecimal amountNow,
        BigDecimal amountLeft,
        BigDecimal budgetFullfillPerc,
        String typeOfBudget,
        String maxValue,
        LocalDateTime timestamp
) {
    public static BudgetStatusDTO newOf(String budgetId,
                                        Integer totalExpensesNumber,
                                        BigDecimal amountNow,
                                        BigDecimal amountLeft,
                                        BigDecimal budgetFullfillPerc,
                                        String typeOfBudget,
                                        String maxValue,
                                        LocalDateTime timestamp) {
        return new BudgetStatusDTO(
                budgetId,
                totalExpensesNumber,
                amountNow,
                amountLeft,
                budgetFullfillPerc,
                typeOfBudget,
                maxValue,
                timestamp);
    }
}
