package com.example.final_project.api.responses;

import java.math.BigDecimal;

public record BudgetStatusDTO(
        BigDecimal amount,
        BigDecimal amountLeft,
        BigDecimal budgetFullfillPerc,
        String typeOfBudget) {

    public static BudgetStatusDTO newOf(BigDecimal amount, BigDecimal amountLeft, BigDecimal budgetFullfillPerc, String typeOfBudget) {
        return new BudgetStatusDTO(amount, amountLeft, budgetFullfillPerc, typeOfBudget);
    }


}
