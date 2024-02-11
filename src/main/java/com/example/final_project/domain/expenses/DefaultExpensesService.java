package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.users.UserId;
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
    public Expense registerNewExpense(String title, BigDecimal amount, BudgetId budgetId, UserId userId, Optional<TypeOfExpense> typeOfExpense) {
        validationForNewExpense(amount, budgetId, userId);

        Expense expense = new Expense(expenseIdSupplier.get(), title, amount, budgetId, userId, LocalDateTime.now(),
                                      typeOfExpense.orElse(TypeOfExpense.NO_CATEGORY));
        expenseRepository.save(expense);
        return expense;
    }

    @Override
    public Optional<Expense> getExpenseById(ExpenseId expenseId, UserId userId) {
        return expenseRepository.findExpenseByExpenseIdAndUserId(expenseId, userId);
    }

    @Override
    public void deleteExpenseById(ExpenseId expenseId, UserId userId) {
        expenseRepository.deleteExpenseByExpenseIdAndUserId(expenseId, userId);
    }

    @Override
    public Optional<Expense> updateExpenseContent(
            ExpenseId expenseId,
            Optional<String> title,
            Optional<BigDecimal> amount,
            UserId userId,
            Optional<TypeOfExpense> typeOfExpense
    ) {

        BudgetId budgetId = expenseRepository.findBudgetByExpenseId(expenseId).budgetId();

        Budget budget = budgetRepository.findBudgetByBudgetIdAndUserId(budgetId, userId).orElseThrow();

        singleMaxExpValidation(amount.orElse(BigDecimal.ZERO), budget);
        checkBudgetLimit(amount.orElse(BigDecimal.ZERO), budget);

        Optional<LocalDateTime> timestamp = Optional.empty();

        expenseRepository.findExpenseByExpenseIdAndUserId(expenseId, userId).map(
                expenseFromRepository -> new Expense(
                        expenseId,
                        title.orElse(expenseFromRepository.title()),
                        amount.orElse(expenseFromRepository.amount()),
                        budget.budgetId(),
                        userId,
                        timestamp.orElse(expenseFromRepository.timestamp()),
                        typeOfExpense.orElse(expenseFromRepository.typeOfExpense())
                )).ifPresent(expenseRepository::save);
        return expenseRepository.findExpenseByExpenseIdAndUserId(expenseId, userId);
    }

    @Override
    public Expense updateExpenseById(
            ExpenseId expenseId,
            BudgetId budgetId,
            String title,
            BigDecimal amount,
            UserId userId,
            TypeOfExpense typeOfExpense
    ) {
        validationForNewExpense(amount, budgetId, userId);

        return expenseRepository.save(new Expense(expenseId,
                title,
                amount,
                budgetId,
                userId,
                LocalDateTime.now(),
                typeOfExpense));
    }
    private void validationForNewExpense(BigDecimal amount, BudgetId budgetId, UserId userId){
        Budget budget = budgetRepository.findBudgetByBudgetIdAndUserId(budgetId, userId).orElseThrow();
        checkBudgetLimit(amount, budget);
        singleMaxExpValidation(amount, budget);
    }

    @Override
    public List<Expense> getExpenses(UserId userId) {
        return expenseRepository.findAllByUserId(userId);
    }

    @Override
    public Page<Expense> findAllExpensesByBudgetId(UserId userId, BudgetId budgetId, Pageable pageable) {
        return expenseRepository.findAllByBudgetIdAndUserId(budgetId, userId, pageable);
    }

    @Override
    public Page<Expense> findAllByPage(Pageable pageable, UserId userId) {
        return expenseRepository.findExpensesByUserId(userId, pageable);
    }

    void singleMaxExpValidation(BigDecimal amount, Budget budget) {
        if (budget.maxSingleExpense().compareTo(amount) < 0) {
            throw new ExpenseTooBigException("Wydatek przekracza możliwy maksymalny wydatek w budżecie!");
        }
    }

    private void checkBudgetLimit(BigDecimal amount, Budget budget) {
        if (budget.typeOfBudget().getValue().compareTo(BigDecimal.valueOf(0)) < 0) {
            return;
        }

        var totalBudgetLimit = budget.limit().multiply(budget.typeOfBudget().getValue());

        var totalExpensesAmount = expenseRepository.findExpensesByBudgetIdAndUserId(
                        budget.budgetId(), budget.userId())
                .stream()
                .map(Expense::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var amountLeft = totalBudgetLimit.subtract(totalExpensesAmount);

        if (amountLeft.compareTo(amount) < 0) {
            throw new ExpenseTooBigException("Wydatek przekroczy limit budzetu!");
        }
    }
}
