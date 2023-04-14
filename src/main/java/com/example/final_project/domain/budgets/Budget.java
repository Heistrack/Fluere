package com.example.final_project.domain.budgets;


import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;

@Document
public record Budget(
        @MongoId
        BudgetId budgetId,
        String title,
        BigDecimal limit,
        TypeOfBudget typeOfBudget,
        BigDecimal maxSingleExpense,
        String userId
) {
}
