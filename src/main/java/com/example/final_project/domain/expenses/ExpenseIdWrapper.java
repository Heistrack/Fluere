package com.example.final_project.domain.expenses;


public record ExpenseIdWrapper(String value) {
    public static ExpenseIdWrapper newId(String value) {
        return new ExpenseIdWrapper(value);
    }
}
