package com.example.final_project.api.responses;

import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.budgets.TypeOfBudget;

import java.math.BigDecimal;

public record BudgetResponseDto(
        String budgetId,
        String title,
        BigDecimal limit,
        TypeOfBudget typeOfBudget,
        BigDecimal maxSingleExpense
) {

    public static BudgetResponseDto fromDomain(Budget budget) {
        return new BudgetResponseDto(
                budget.budgetId().value(),
                budget.title(),
                budget.limit(),
                budget.typeOfBudget(),
                budget.maxSingleExpense()
        );

    }

}
