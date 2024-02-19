package com.example.final_project.domain.expenses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

public record ExpenseDetails(
        String title,
        BigDecimal amount,
        TreeMap<Integer, LocalDateTime> historyOfChanges,
        ExpenseType expenseType
) {
    public static ExpenseDetails newOf(String title, BigDecimal amount,
                                       TreeMap<Integer, LocalDateTime> historyOfChanges,
                                       ExpenseType expenseType
    ) {
        return new ExpenseDetails(title, amount, historyOfChanges, expenseType);
    }
}
