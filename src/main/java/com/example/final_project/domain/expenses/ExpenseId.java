package com.example.final_project.domain.expenses;


public record ExpenseId(String value) {
    public static ExpenseId newId(String value) {
        return new ExpenseId(value);
    }
}
