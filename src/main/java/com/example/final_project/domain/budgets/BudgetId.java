package com.example.final_project.domain.budgets;

import lombok.Data;

public record BudgetId(String value) {

    public static BudgetId newOf(String value){
        return new BudgetId(value);
    }

}
