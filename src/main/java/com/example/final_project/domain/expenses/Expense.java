package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.BudgetId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document
public record Expense(
        @Id ExpenseId expenseId,
        String title,
        BigDecimal amount,
        BudgetId budgetId,
        String userId,
        LocalDateTime timestamp,
        TypeOfExpense typeOfExpense
) {
}
