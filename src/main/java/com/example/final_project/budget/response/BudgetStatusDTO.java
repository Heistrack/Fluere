package com.example.final_project.budget.response;

import com.example.final_project.budget.service.BudgetDetails;
import com.example.final_project.expense.service.Expense;
import com.example.final_project.expense.service.ExpenseType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

public record BudgetStatusDTO(
        UUID budgetId,
        BudgetDetails budgetDetails,
        BigDecimal amountLeft,
        BigDecimal totalMoneySpent,
        Integer totalExpensesNumber,
        Float budgetFullFillPercent,
        String trueLimitValue,
        TreeMap<LocalDate, List<Expense>> dayExpenses,
        HashMap<ExpenseType, List<Expense>> categoryExpenses,
        HashMap<ExpenseType, Float> categoryExpensesPercentage
) {
    public static BudgetStatusDTO newOf(UUID budgetId,
                                        BudgetDetails budgetDetails,
                                        BigDecimal amountLeft,
                                        BigDecimal totalMoneySpent,
                                        Integer totalExpensesNumber,
                                        Float budgetFullFillPercent,
                                        String trueLimitValue,
                                        TreeMap<LocalDate, List<Expense>> dayExpenses,
                                        HashMap<ExpenseType, List<Expense>> categoryExpenses,
                                        HashMap<ExpenseType, Float> categoryExpensesPercentage
    ) {
        return new BudgetStatusDTO(
                budgetId,
                budgetDetails,
                amountLeft,
                totalMoneySpent,
                totalExpensesNumber,
                budgetFullFillPercent,
                trueLimitValue,
                dayExpenses,
                categoryExpenses,
                categoryExpensesPercentage
        );
    }
}
