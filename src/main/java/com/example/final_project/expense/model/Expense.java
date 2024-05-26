package com.example.final_project.expense.model;

import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.userentity.model.UserIdWrapper;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.hateoas.RepresentationModel;

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
