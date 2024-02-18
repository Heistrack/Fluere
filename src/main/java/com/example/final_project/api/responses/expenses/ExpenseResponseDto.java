package com.example.final_project.api.responses.expenses;

import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.TypeOfExpense;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

public record ExpenseResponseDto(
        String title,
        String expenseId,
        BigDecimal amount,
        TreeMap<Integer, LocalDateTime> historyOfChanges,
        TypeOfExpense typeOfExpense
) {

    public static ExpenseResponseDto fromDomain(Expense expense) {
        return new ExpenseResponseDto(
                expense.expenseDetails().title(),
                expense.expenseId().id().toString(),
                expense.expenseDetails().amount(),
                expense.expenseDetails().historyOfChanges(),
                expense.expenseDetails().typeOfExpense()
        );
    }
}
