package com.example.final_project.domain.expenses;


import java.util.UUID;

public record ExpenseIdWrapper(UUID id) {
    public static ExpenseIdWrapper newOf(UUID id) {
        return new ExpenseIdWrapper(id);
    }
}
