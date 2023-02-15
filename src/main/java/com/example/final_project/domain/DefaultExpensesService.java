package com.example.final_project.domain;

import com.example.final_project.infrastructure.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class DefaultExpensesService implements ExpensesService{

    private final ExpenseRepository expenseRepository;
    private final Supplier<ExpenseId> expenseIdSupplier;

    public DefaultExpensesService(ExpenseRepository expenseRepository, Supplier<ExpenseId> expenseIdSupplier) {
        this.expenseRepository = expenseRepository;
        this.expenseIdSupplier = expenseIdSupplier;
    }

    @Override
    public Expense registerNewExpense(String title, BigDecimal amount) {
        Expense expense = new Expense(expenseIdSupplier.get(), title, amount);
        expenseRepository.save(expense);
        return expense;
    }

    @Override
    public Optional<Expense> getExpenseById(ExpenseId expenseId) {
        return expenseRepository.getExpenseById(expenseId);
    }

    @Override
    public void deleteExpenseById(ExpenseId expenseId) {
        expenseRepository.deleteExpenseById(expenseId);
    }
}
