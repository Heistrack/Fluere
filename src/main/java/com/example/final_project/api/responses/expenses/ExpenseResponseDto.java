package com.example.final_project.api.responses.expenses;

import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.TypeOfExpense;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseResponseDto(
        String title,
        String expenseId,
        BigDecimal amount,
        LocalDateTime timestamp,
        TypeOfExpense typeOfExpense
) {

    public static ExpenseResponseDto fromDomain(Expense expense) {
        return new ExpenseResponseDto(
                expense.title(),
                expense.expenseId().id().toString(),
                expense.amount(),
                expense.timestamp(),
                expense.typeOfExpense()
        );
    }
}
