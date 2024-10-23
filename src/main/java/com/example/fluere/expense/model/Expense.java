package com.example.fluere.expense.model;

import com.example.fluere.budget.model.BudgetIdWrapper;
import com.example.fluere.userentity.model.UserIdWrapper;
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
