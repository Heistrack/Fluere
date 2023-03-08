package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.BudgetId;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
@Document
public record Expense(
        @Id ExpenseId expenseId,
        String title,
        BigDecimal amount,
        BudgetId budgetId,
        String userId
) {
}
