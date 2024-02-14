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
        // such a binding through expenseId -> budgetId -> UserId
        UserIdWrapper userId,
        LocalDateTime timestamp,
        TypeOfExpense typeOfExpense
) {
}
