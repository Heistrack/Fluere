package com.example.final_project.domain;

import com.example.final_project.infrastructure.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class DefaultExpensesService implements ExpensesService {

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
        return expenseRepository.findExpenseByExpenseId(expenseId);
    }

    @Override
    public void deleteExpenseById(ExpenseId expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    @Override
    public Optional<Expense> updateExpenseContent(ExpenseId expenseId, Optional<String> title, Optional<BigDecimal> amount) {
        expenseRepository.findExpenseByExpenseId(expenseId).map(
                expenseFromRepository -> new Expense(expenseId, title.orElse(expenseFromRepository.title()), amount.orElse(expenseFromRepository.amount())
                )).ifPresent(expenseRepository::save);
        return expenseRepository.findExpenseByExpenseId(expenseId);
    }

    @Override
    public List<Expense> getExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    public Expense updateExpenseById(ExpenseId expenseId, String title, BigDecimal amount) {
        return expenseRepository.save(new Expense(expenseId, title, amount));
    }
}
