package com.example.final_project.domain.budgets;


import com.example.final_project.domain.users.UserId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document
public record Budget(
        @MongoId
        BudgetId budgetId,
        String title,
        BigDecimal limit,
        TypeOfBudget typeOfBudget,
        BigDecimal maxSingleExpense,
        UserId userId,
        LocalDateTime registerTime
) {
}
