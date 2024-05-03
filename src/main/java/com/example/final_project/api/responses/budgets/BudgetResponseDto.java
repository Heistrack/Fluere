package com.example.final_project.api.responses.budgets;

import com.example.final_project.domain.budgets.appusers.Budget;
import com.example.final_project.domain.budgets.appusers.BudgetPeriod;
import com.example.final_project.domain.budgets.appusers.BudgetType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.UUID;

public record BudgetResponseDto(
        UUID budgetId,
        String title,
        BigDecimal limit,
        BudgetType budgetType,
        BigDecimal maxSingleExpense,
        TreeMap<Integer, LocalDateTime> historyOfChanges,
        BudgetPeriod budgetPeriod,
        String description
) {

    public static BudgetResponseDto fromDomain(Budget budget) {
        return new BudgetResponseDto(
                budget.budgetId().id(),
                budget.budgetDetails().title(),
                budget.budgetDetails().limit(),
                budget.budgetDetails().budgetType(),
                budget.budgetDetails().maxSingleExpense(),
                budget.budgetDetails().historyOfChanges(),
                budget.budgetDetails().budgetPeriod(),
                budget.budgetDetails().description()
        );
    }
}
