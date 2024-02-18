package com.example.final_project.domain.budgets;


import com.example.final_project.domain.users.UserIdWrapper;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

@Document
public record Budget(
        @MongoId
        BudgetIdWrapper budgetId,
        UserIdWrapper userId,
        BudgetDetails budgetDetails
) {
    public static Budget newOf(BudgetIdWrapper budgetId, UserIdWrapper userId, BudgetDetails budgetDetails) {
        return new Budget(budgetId, userId, budgetDetails);
    }
}
