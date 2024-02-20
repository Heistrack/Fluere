package com.example.final_project.domain.budgets.appusers;


import com.example.final_project.domain.users.appusers.UserIdWrapper;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

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
