package com.example.final_project.domain.budgets;


import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
public record Budget(
        @Id BudgetId budgetId,
        String title,
        BigDecimal limit,
        TypeOfBudget typeOfBudget,
        BigDecimal maxSingleExpense,
        String userId

) {
}
