package com.example.final_project.expense.model;

import com.example.final_project.currencyapi.model.MKTCurrency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

public record ExpenseDetails(
        String title,
        BigDecimal amount,
        MKTCurrency currency,
        TreeMap<Integer, LocalDateTime> historyOfChanges,
        ExpenseType expenseType,
        String description
) {
    public static ExpenseDetails newOf(String title, BigDecimal amount,
                                       MKTCurrency currency,
                                       TreeMap<Integer, LocalDateTime> historyOfChanges,
                                       ExpenseType expenseType,
                                       String description
    ) {
        return new ExpenseDetails(title, amount, currency, historyOfChanges, expenseType, description);
    }
}
