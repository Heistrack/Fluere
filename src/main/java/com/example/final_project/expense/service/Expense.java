package com.example.final_project.expense.service;

import com.example.final_project.budget.service.BudgetIdWrapper;
import com.example.final_project.userentity.service.UserIdWrapper;
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
