package com.example.final_project.budget.response;

import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.BudgetPeriod;
import com.example.final_project.budget.model.BudgetType;
import com.example.final_project.budget.model.ExpenseSet;
import com.example.final_project.currencyapi.model.MKTCurrency;

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
        MKTCurrency defaultCurrency,
        ExpenseSet expenseSet,
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
                budget.budgetDetails().defaultCurrency(),
                budget.budgetDetails().expenseSet(),
                budget.budgetDetails().historyOfChanges(),
                budget.budgetDetails().budgetPeriod(),
                budget.budgetDetails().description()
        );
    }
}
