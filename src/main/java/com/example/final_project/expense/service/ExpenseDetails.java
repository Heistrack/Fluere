package com.example.final_project.expense.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

public record ExpenseDetails(
        String title,
        BigDecimal amount,
        TreeMap<Integer, LocalDateTime> historyOfChanges,
        ExpenseType expenseType,
        String description
) {
    public static ExpenseDetails newOf(String title, BigDecimal amount,
                                       TreeMap<Integer, LocalDateTime> historyOfChanges,
                                       ExpenseType expenseType,
                                       String description
    ) {
        return new ExpenseDetails(title, amount, historyOfChanges, expenseType, description);
    }
}
