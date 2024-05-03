package com.example.final_project.expense.response.admin;

import com.example.final_project.expense.service.Expense;
import com.example.final_project.expense.service.ExpenseType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

public record AdminExpenseResponseDto(
        String title,
        String expenseId,
        String budgetId,
        BigDecimal amount,
        TreeMap<Integer, LocalDateTime> historyOfChanges,
        ExpenseType expenseType,
        String description
) {
    public static AdminExpenseResponseDto fromDomain(Expense expense) {
        return new AdminExpenseResponseDto(
                expense.expenseDetails().title(),
                expense.expenseId().id().toString(),
                expense.budgetId().id().toString(),
                expense.expenseDetails().amount(),
                expense.expenseDetails().historyOfChanges(),
                expense.expenseDetails().expenseType(),
                expense.expenseDetails().description()
        );
    }
}