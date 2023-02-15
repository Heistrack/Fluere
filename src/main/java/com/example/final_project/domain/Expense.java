package com.example.final_project.domain;

import java.math.BigDecimal;

public record Expense(ExpenseId expenseId, String title, BigDecimal amount) {
}
