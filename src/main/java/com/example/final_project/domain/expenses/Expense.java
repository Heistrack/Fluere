package com.example.final_project.domain.expenses;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
@Document
public record Expense(@Id ExpenseId expenseId, String title, BigDecimal amount) {
}
