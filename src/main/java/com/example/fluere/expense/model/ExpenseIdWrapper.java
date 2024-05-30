package com.example.fluere.expense.model;


import java.util.UUID;

public record ExpenseIdWrapper(UUID id) {
    public static ExpenseIdWrapper newOf(UUID id) {
        return new ExpenseIdWrapper(id);
    }

    public static ExpenseIdWrapper newFromString(String rawStringExpenseId) {
        return new ExpenseIdWrapper(UUID.fromString(rawStringExpenseId));
    }
}
