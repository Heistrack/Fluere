package com.example.final_project.expense.response.appuser;

import com.example.final_project.budget.model.MKTCurrency;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

public record ExpenseResponseDto(
        String title,
        String expenseId,
        BigDecimal amount,
        MKTCurrency currency,
        TreeMap<Integer, LocalDateTime> historyOfChanges,
        ExpenseType expenseType,
        String description
) {
    public static ExpenseResponseDto fromDomain(Expense expense) {
        return new ExpenseResponseDto(
                expense.expenseDetails().title(),
                expense.expenseId().id().toString(),
                expense.expenseDetails().amount(),
                expense.expenseDetails().currency(),
                expense.expenseDetails().historyOfChanges(),
                expense.expenseDetails().expenseType(),
                expense.expenseDetails().description()
        );
    }
}
