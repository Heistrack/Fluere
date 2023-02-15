package com.example.final_project.domain;

public record ExpenseId(String value) {

    static ExpenseId newId(String value) {
        return new ExpenseId(value);
    }

}
