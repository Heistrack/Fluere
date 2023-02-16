package com.example.final_project.infrastructure;

import com.example.final_project.domain.Expense;
import com.example.final_project.domain.ExpenseId;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface ExpenseRepository {
    Expense save(Expense expense);

    Optional<Expense> getExpenseById(ExpenseId expenseId);

    void deleteExpenseById(ExpenseId expenseId);

    List<Expense> getAllExpenses();

    Expense updateExpenseById(Expense expense);

}
