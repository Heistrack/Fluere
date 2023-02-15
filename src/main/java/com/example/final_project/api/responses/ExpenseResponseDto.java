package com.example.final_project.api.responses;

import com.example.final_project.domain.Expense;

import java.math.BigDecimal;

public record ExpenseResponseDto(
        String title,
        String expenseId,
        BigDecimal amount
) {

    public static ExpenseResponseDto fromDomain(Expense expense) {
        return new ExpenseResponseDto(
                expense.title(),
                expense.expenseId().value(),
                expense.amount()
        );
    }

}
