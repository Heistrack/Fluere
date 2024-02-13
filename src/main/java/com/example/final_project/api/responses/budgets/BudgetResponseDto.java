package com.example.final_project.api.responses.budgets;

import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.budgets.TypeOfBudget;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BudgetResponseDto(
        BudgetId budgetId,
        String title,
        BigDecimal limit,
        TypeOfBudget typeOfBudget,
        BigDecimal maxSingleExpense,
        LocalDateTime timestamp
) {

    public static BudgetResponseDto fromDomain(Budget budget) {
        return new BudgetResponseDto(
                budget.budgetId(),
                budget.title(),
                budget.limit(),
                budget.typeOfBudget(),
                budget.maxSingleExpense(),
                budget.registerTime()
        );
    }
}
