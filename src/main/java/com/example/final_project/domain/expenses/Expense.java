package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document
public record Expense(
        @MongoId ExpenseIdWrapper expenseId,
        BudgetIdWrapper budgetId,
        UserIdWrapper userId,
        ExpenseDetails expenseDetails
) {
    @Builder
    public Expense {
    }

    public static Expense newOf(ExpenseIdWrapper expenseId, BudgetIdWrapper budgetId, UserIdWrapper userId,
                                ExpenseDetails expenseDetails
    ) {
        return new Expense(expenseId, budgetId, userId, expenseDetails);
    }
}
