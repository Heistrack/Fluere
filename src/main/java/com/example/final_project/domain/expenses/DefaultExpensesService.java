package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
import com.example.final_project.infrastructure.exprepo.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class DefaultExpensesService implements ExpensesService {

    private final ExpenseRepository expenseRepository;
    private final Supplier<ExpenseId> expenseIdSupplier;
    private final BudgetRepository budgetRepository;

    public DefaultExpensesService(ExpenseRepository expenseRepository, Supplier<ExpenseId> expenseIdSupplier, BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseIdSupplier = expenseIdSupplier;
        this.budgetRepository = budgetRepository;
    }


    @Override
    public Expense registerNewExpense(String title, BigDecimal amount, BudgetId budgetId, String userId) {
        Budget budget = budgetRepository.findBudgetByBudgetIdAndUserId(budgetId, userId).orElseThrow();

        singleMaxExpValidation(amount, budget);
        checkBudgetLimit(amount, budget);

        BigDecimal totalAmount = expenseRepository.findExpenseByBudgetIdAndUserId(budget.budgetId(), userId)
                .stream()
                .map(Expense::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Expense expense = new Expense(expenseIdSupplier.get(), title, amount, budgetId, userId, LocalDateTime.now());
        expenseRepository.save(expense);
        return expense;
    }

    @Override
    public Optional<Expense> getExpenseById(ExpenseId expenseId, String userId) {
        return expenseRepository.findExpenseByExpenseIdAndUserId(expenseId, userId);
    }

    @Override
    public void deleteExpenseById(ExpenseId expenseId, String userId) {
        expenseRepository.deleteExpenseByExpenseIdAndUserId(expenseId, userId);
    }

    @Override
    public Optional<Expense> updateExpenseContent(ExpenseId expenseId, Optional<String> title, Optional<BigDecimal> amount, String userId) {
        var budget = expenseRepository.findBudgetByExpenseId(expenseId);
        singleMaxExpValidation(amount.get(), budget);
        checkBudgetLimit(amount.get(), budget);
        Optional<LocalDateTime> timestamp = Optional.empty();

        expenseRepository.findExpenseByExpenseIdAndUserId(expenseId, userId).map(
                expenseFromRepository -> new Expense(expenseId,
                        title.orElse(expenseFromRepository.title()),
                        amount.orElse(expenseFromRepository.amount()),
                        null,
                        userId,
                        timestamp.orElse(expenseFromRepository.timestamp())
                )).ifPresent(expenseRepository::save);
        return expenseRepository.findExpenseByExpenseIdAndUserId(expenseId, userId);
    }


    @Override
    public List<Expense> getExpenses(String userId) {
        return expenseRepository.findAllByUserId(userId);
    }

    @Override
    public Expense updateExpenseById(ExpenseId expenseId, String title, BigDecimal amount, String userId) {
        return expenseRepository.save(new Expense(expenseId, title, amount, null, userId, LocalDateTime.now()));
    }

    @Override
    public Page<Expense> findAllByPage(Pageable pageable, String userId) {
        return expenseRepository.findExpensesByUserId(userId, pageable);
    }

    void singleMaxExpValidation(BigDecimal amount, Budget budget) {
        if (budget.maxSingleExpense().compareTo(amount) < 0) {
            throw new ExpenseTooBigException("Wydatek przekracza możliwy maksymalny wydatek w budżecie!");
        }
    }

    void checkBudgetLimit(BigDecimal amount, Budget budget) {
        if (budget.typeOfBudget().getValue().compareTo(BigDecimal.valueOf(0)) < 0) {
            return;
        }

        var totalBudgetLimit = budget.limit().multiply(budget.typeOfBudget().getValue());

        var totalExpensesAmount = expenseRepository.findExpensesByBudgetIdAndUserId(budget.budgetId(), budget.userId())
                .stream()
                .map(Expense::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var amountLeft = totalBudgetLimit.subtract(totalExpensesAmount);

        if (amountLeft.compareTo(amount) < 0) {
            throw new ExpenseTooBigException("Wydatek przekroczy limit budzetu!");
        }
    }
}
