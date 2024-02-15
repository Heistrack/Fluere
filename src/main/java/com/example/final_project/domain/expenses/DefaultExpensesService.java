package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetIdWrapper;
import com.example.final_project.domain.users.UserIdWrapper;
import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
import com.example.final_project.infrastructure.exprepo.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class DefaultExpensesService implements ExpensesService {

    private final ExpenseRepository expenseRepository;
    private final Supplier<ExpenseIdWrapper> expenseIdSupplier;
    private final BudgetRepository budgetRepository;

    @Override
    public void deleteExpenseById(ExpenseIdWrapper expenseId, UserIdWrapper userId) {
        expenseRepository.deleteByExpenseIdAndUserId(expenseId, userId);
    }

    @Override
    public Expense getExpenseById(ExpenseIdWrapper expenseId, UserIdWrapper userId) {
        return expenseRepository.findByExpenseIdAndUserId(expenseId, userId)
                                .orElseThrow(() -> new NoSuchElementException("Expense doesn't exist."));
    }

    @Override
    public Expense registerNewExpense(String title, BigDecimal amount, BudgetIdWrapper budgetId, UserIdWrapper userId,
                                      Optional<TypeOfExpense> typeOfExpense
    ) {
        validationForNewExpense(amount, budgetId, userId);

        Expense expense = new Expense(expenseIdSupplier.get(), title, amount, budgetId, userId, LocalDateTime.now(),
                                      typeOfExpense.orElse(TypeOfExpense.NO_CATEGORY)
        );
        return expenseRepository.save(expense);
    }

    @Override
    public Expense patchExpenseContent(
            ExpenseIdWrapper expenseId,
            Optional<String> title,
            Optional<BigDecimal> amount,
            UserIdWrapper userId,
            Optional<TypeOfExpense> typeOfExpense
    ) {
        //TODO if findByExpenseId find budgets?? and do I really need to find in separate repo
        BudgetIdWrapper budgetId = expenseRepository.findByExpenseId(expenseId)
                                                    .orElseThrow(
                                                            () -> new NoSuchElementException("There's no such budget."))
                                                    .budgetId();

        Budget budget = budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                                        .orElseThrow(() -> new NoSuchElementException("There is no such budget."));

        singleMaxExpValidation(amount.orElse(BigDecimal.ZERO), budget);
        checkBudgetLimit(amount.orElse(BigDecimal.ZERO), budget);
        //TODO add hashmap to expense and budget put and patch for history of changes linked hashmap
        Optional<LocalDateTime> timestamp = Optional.empty();

        expenseRepository.findByExpenseIdAndUserId(expenseId, userId).map(
                expenseFromRepository -> Expense.newOf(
                        expenseId,
                        title.orElseGet(expenseFromRepository::title),
                        amount.orElseGet(expenseFromRepository::amount),
                        budget.budgetId(),
                        userId,
                        timestamp.orElseGet(expenseFromRepository::timeOfCreation),
                        typeOfExpense.orElseGet(expenseFromRepository::typeOfExpense)
                )).ifPresent(expenseRepository::save);

        return expenseRepository.findByExpenseIdAndUserId(expenseId, userId).get();
    }

    @Override
    public Expense updateExpenseById(
            ExpenseIdWrapper expenseId,
            BudgetIdWrapper budgetId,
            String title,
            BigDecimal amount,
            UserIdWrapper userId,
            Optional<TypeOfExpense> typeOfExpense
    ) {
        validationForNewExpense(amount, budgetId, userId);
        //TODO add linked hash map for all timestamps
        LocalDateTime now = LocalDateTime.now();

        return expenseRepository.save(Expense.newOf(
                expenseId,
                title,
                amount,
                budgetId,
                userId,
                now,
                typeOfExpense.orElse(TypeOfExpense.NO_CATEGORY)
        ));
    }

    @Override
    public Page<Expense> findAllExpensesByBudgetId(UserIdWrapper userId, BudgetIdWrapper budgetId, Pageable pageable) {
        return expenseRepository.findAllByBudgetIdAndUserId(budgetId, userId, pageable);
    }

    @Override
    public Page<Expense> findAllByPage(UserIdWrapper userId, Pageable pageable) {
        return expenseRepository.findAllByUserId(userId, pageable);
    }

    private void validationForNewExpense(BigDecimal amount, BudgetIdWrapper budgetId, UserIdWrapper userId) {
        Budget checkBudget = budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                                             .orElseThrow(() -> new NoSuchElementException("There's no such budget."));
        checkBudgetLimit(amount, checkBudget);
        singleMaxExpValidation(amount, checkBudget);
    }

    private void singleMaxExpValidation(BigDecimal amount, Budget budget) {
        if (budget.maxSingleExpense().compareTo(amount) < 0) {
            throw new ExpenseTooBigException("Expense exceed single maximal expense amount in the budget!");
        }
    }

    private void checkBudgetLimit(BigDecimal amount, Budget budget) {
        if (budget.typeOfBudget().getValue().compareTo(BigDecimal.valueOf(0)) < 0) {
            return;
        }

        BigDecimal totalBudgetLimit = budget.limit().multiply(budget.typeOfBudget().getValue());

        BigDecimal totalExpensesSum = expenseRepository.findAllByBudgetIdAndUserId(
                                                               budget.budgetId(), budget.userId())
                                                       .stream()
                                                       .map(Expense::amount)
                                                       .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal amountLeft = totalBudgetLimit.subtract(totalExpensesSum);

        if (amountLeft.compareTo(amount) < 0) {
            throw new ExpenseTooBigException("Expense exceed the budget limit!");
        }
    }
}
