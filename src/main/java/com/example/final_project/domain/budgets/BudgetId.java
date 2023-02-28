package com.example.final_project.domain.budgets;

public record BudgetId(String value) {

    public static BudgetId newOf(String value){
        return new BudgetId(value);
    }

}
