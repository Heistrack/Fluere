package com.example.final_project.domain.budgets;

import java.util.UUID;

public record BudgetIdWrapper(UUID id) {

    public static BudgetIdWrapper newOf(UUID id) {
        return new BudgetIdWrapper(id);
    }

    public static BudgetIdWrapper newFromString(String rawStringId) {
        return new BudgetIdWrapper(UUID.fromString(rawStringId));
    }
}
