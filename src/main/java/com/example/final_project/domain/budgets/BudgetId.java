package com.example.final_project.domain.budgets;

import java.util.UUID;

public record BudgetId(UUID budgetId) {

    public static BudgetId newOf(UUID id) {
        return new BudgetId(id);
    }

    public static BudgetId newFromString(String id) {
        return new BudgetId(UUID.fromString(id));
    }
}
