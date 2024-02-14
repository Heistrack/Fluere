package com.example.final_project.domain.budgets;


import com.example.final_project.domain.users.UserIdWrapper;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document
public record Budget(
        @MongoId
        BudgetIdWrapper budgetId,
        String title,
        BigDecimal limit,
        TypeOfBudget typeOfBudget,
        BigDecimal maxSingleExpense,
        UserIdWrapper userId,
        LocalDateTime registerTime
) {
    public static Budget newOf(BudgetIdWrapper budgetId, String title, BigDecimal limit, TypeOfBudget typeOfBudget,
                               BigDecimal maxSingleExpense, UserIdWrapper userId, LocalDateTime registerTime
    ) {
        return new Budget(budgetId, title, limit, typeOfBudget, maxSingleExpense, userId, registerTime);
    }
}
