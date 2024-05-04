package com.example.final_project.expense.model;


import java.util.UUID;

public record ExpenseIdWrapper(UUID id) {
    public static ExpenseIdWrapper newOf(UUID id) {
        return new ExpenseIdWrapper(id);
    }
}
