package com.example.final_project.domain.budgets;

import com.example.final_project.domain.budgets.BudgetId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
public record Budget(BudgetId budgetId, String title, BigDecimal limit, TypeOfBudget typeOfBudget, BigDecimal maxSingleExpense) {
}
