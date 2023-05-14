package com.example.final_project.api.responses;

import com.example.final_project.domain.expenses.Expense;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseResponseDto(
        String title,
        String expenseId,
        BigDecimal amount,
        LocalDateTime timestamp
) {

    public static ExpenseResponseDto fromDomain(Expense expense) {
        return new ExpenseResponseDto(
                expense.title(),
                expense.expenseId().value(),
                expense.amount(),
                expense.timestamp()
        );
    }
}
