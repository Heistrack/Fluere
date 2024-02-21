package com.example.final_project.api.responses.expenses.admins;

import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

public record AdminExpenseResponseDto(
        String title,
        String expenseId,
        String budgetId,
        BigDecimal amount,
        TreeMap<Integer, LocalDateTime> historyOfChanges,
        ExpenseType expenseType
) {
    public static AdminExpenseResponseDto fromDomain(Expense expense) {
        return new AdminExpenseResponseDto(
                expense.expenseDetails().title(),
                expense.expenseId().id().toString(),
                expense.budgetId().id().toString(),
                expense.expenseDetails().amount(),
                expense.expenseDetails().historyOfChanges(),
                expense.expenseDetails().expenseType()
        );
    }
}
