package com.example.final_project.domain.budgets;

import java.util.UUID;

public record BudgetIdWrapper(UUID budgetId) {

    public static BudgetIdWrapper newOf(UUID id) {
        return new BudgetIdWrapper(id);
    }

    public static BudgetIdWrapper newFromString(String id) {
        return new BudgetIdWrapper(UUID.fromString(id));
    }
}
