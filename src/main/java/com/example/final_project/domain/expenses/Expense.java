package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.BudgetIdWrapper;
import com.example.final_project.domain.users.UserIdWrapper;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document
public record Expense(
        @MongoId ExpenseIdWrapper expenseId,
        String title,
        BigDecimal amount,
        BudgetIdWrapper budgetId,
        //FIXME Do I really need bind expenses with users if I have already got
        // such a binding through expenseId -> id -> UserId
        UserIdWrapper userId,
        LocalDateTime timeOfCreation,
        TypeOfExpense typeOfExpense
) {
    public static Expense newOf(ExpenseIdWrapper expenseId, String title, BigDecimal amount, BudgetIdWrapper budgetId,
                                UserIdWrapper userId, LocalDateTime timeOfCreation, TypeOfExpense typeOfExpense
    ) {
        return new Expense(expenseId, title, amount, budgetId, userId, timeOfCreation, typeOfExpense);
    }
}
