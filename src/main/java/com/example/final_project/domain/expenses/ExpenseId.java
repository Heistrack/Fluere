package com.example.final_project.domain.expenses;

import lombok.Data;

public record ExpenseId(String value) {

    static ExpenseId newId(String value) {
        return new ExpenseId(value);
    }

}
