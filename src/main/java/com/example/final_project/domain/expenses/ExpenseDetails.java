package com.example.final_project.domain.expenses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

public record ExpenseDetails(
        String title,
        BigDecimal amount,
        TreeMap<Integer, LocalDateTime> historyOfChanges,
        TypeOfExpense typeOfExpense
) {
    public static ExpenseDetails newOf(String title, BigDecimal amount,
                                       TreeMap<Integer, LocalDateTime> historyOfChanges,
                                       TypeOfExpense typeOfExpense
    ) {
        return new ExpenseDetails(title, amount, historyOfChanges, typeOfExpense);
    }
}
