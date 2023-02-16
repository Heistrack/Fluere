package com.example.final_project.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExpensesService {
    Expense registerNewExpense(String title, BigDecimal amount);
    Optional<Expense> getExpenseById(ExpenseId expenseId);

    void deleteExpenseById(ExpenseId expenseId);

    List<Expense> getExpenses();

    Expense updateExpenseById(ExpenseId expenseId, String title, BigDecimal amount);
}
