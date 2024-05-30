package com.example.fluere.budget.model;

import java.util.UUID;

public record BudgetIdWrapper(UUID id) {

    public static BudgetIdWrapper newOf(UUID id) {
        return new BudgetIdWrapper(id);
    }

    public static BudgetIdWrapper newFromString(String rawStringBudgetId) {
        return new BudgetIdWrapper(UUID.fromString(rawStringBudgetId));
    }
}