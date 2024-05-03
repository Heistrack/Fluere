package com.example.final_project.expense.service.admin;

import com.example.final_project.budget.service.Budget;
import com.example.final_project.budget.service.BudgetIdWrapper;
import com.example.final_project.expense.service.Expense;
import com.example.final_project.expense.service.ExpenseDetails;
import com.example.final_project.expense.service.ExpenseIdWrapper;
import com.example.final_project.expense.service.ExpenseType;
import com.example.final_project.exception.custom.ExpenseTooBigException;
import com.example.final_project.userentity.service.UserIdWrapper;
import com.example.final_project.budget.repository.BudgetRepository;
import com.example.final_project.expense.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AdminDefaultExpenseService implements AdminExpenseService {

    private final ExpenseRepository expenseRepository;
    private final Supplier<ExpenseIdWrapper> expenseIdSupplier;
    private final BudgetRepository budgetRepository;

    @Override
    public Expense registerNewExpense(BudgetIdWrapper budgetId, UserIdWrapper userId, String title,
                                      BigDecimal amount, ExpenseType expenseType, String description
    ) {
        String checkedTitle = duplicateExpenseTitleCheck(title, budgetId);
        validationExpenseAmount(amount, budgetId);

        TreeMap<Integer, LocalDateTime> historyOfChange = new TreeMap<>();
        historyOfChange.put(1, LocalDateTime.now());
        budgetRepository.findById(budgetId).map(Budget::userId);

        Expense expense = Expense.newOf(
                expenseIdSupplier.get(), budgetId, userId, ExpenseDetails.newOf(checkedTitle, amount,
                                                                                historyOfChange,
                                                                                expenseType,
                                                                                description == null ? "" : description
                ));
        return expenseRepository.save(expense);
    }

    @Override
    public Expense getExpenseById(ExpenseIdWrapper expenseId) {
        return expenseRepository.findById(expenseId)
                                .orElseThrow(() -> new NoSuchElementException("Expense doesn't exist."));
    }

    @Override
    public Page<Expense> getAllExpensesByBudgetId(BudgetIdWrapper budgetId, Pageable pageable) {
        return expenseRepository.findAllByBudgetId(budgetId, pageable);
    }

    @Override
    public Page<Expense> getAllExpensesByUserId(UserIdWrapper userId, Pageable pageable) {
        return expenseRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Page<Expense> getAllExpensesByPage(Pageable pageable) {
        return expenseRepository.findAll(pageable);
    }

    @Override
    public Expense updateExpenseById(
            ExpenseIdWrapper expenseId,
            String title,
            BigDecimal amount,
            Optional<ExpenseType> expenseType,
            Optional<String> description
    ) {
        Expense oldExpense = expenseRepository.findById(expenseId).orElseThrow(
                () -> new NoSuchElementException("Expense doesn't exist."));

        if (noParamChangeCheck(oldExpense, Optional.of(title), Optional.of(amount), expenseType, description)) {
            return oldExpense;
        }
        if (!title.equals(oldExpense.expenseDetails().title())) {
            title = duplicateExpenseTitleCheck(title, oldExpense.budgetId());
        }
        if (!amount.equals(oldExpense.expenseDetails().amount())) {
            validationExpenseAmount(amount, oldExpense.budgetId());
        }

        updateHistoryChange(oldExpense);

        return expenseRepository.save(Expense.newOf(
                expenseId,
                oldExpense.budgetId(),
                oldExpense.userId(),
                ExpenseDetails.newOf(
                        title,
                        amount,
                        oldExpense.expenseDetails().historyOfChanges(),
                        expenseType.orElse(ExpenseType.NO_CATEGORY),
                        description.orElse("")
                )
        ));
    }

    @Override
    public Expense patchExpenseContent(
            ExpenseIdWrapper expenseId,
            Optional<String> title,
            Optional<BigDecimal> amount,
            Optional<ExpenseType> expenseType,
            Optional<String> description
    ) {
        Expense oldExpense = expenseRepository.findById(expenseId)
                                              .orElseThrow(() -> new NoSuchElementException(
                                                      "There's no such expense."));

        if (noParamChangeCheck(oldExpense, title, amount, expenseType, description)) {
            return oldExpense;
        }
        if (title.isPresent() && !title.get().equals(oldExpense.expenseDetails().title())) {
            title = Optional.of(duplicateExpenseTitleCheck(title.get(), oldExpense.budgetId()));
        }
        if (amount.isPresent() && !amount.get().equals(oldExpense.expenseDetails().amount())) {
            validationExpenseAmount(amount.get(), oldExpense.budgetId());
        }

        updateHistoryChange(oldExpense);
        Optional<String> checkedTitle = title;

        return expenseRepository.save(expenseRepository.findById(expenseId).map(
                expenseFromRepository -> Expense.newOf(
                        expenseId,
                        oldExpense.budgetId(),
                        oldExpense.userId(),
                        ExpenseDetails.newOf(
                                checkedTitle.orElseGet(() -> expenseFromRepository.expenseDetails().title()),
                                amount.orElseGet(() -> expenseFromRepository.expenseDetails().amount()),
                                oldExpense.expenseDetails().historyOfChanges(),
                                expenseType.orElseGet(() -> expenseFromRepository.expenseDetails().expenseType()),
                                description.orElseGet(() -> expenseFromRepository.expenseDetails().description())
                        )
                )
        ).orElseThrow(IllegalArgumentException::new));
    }

    @Override
    public void deleteExpenseById(ExpenseIdWrapper expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    private boolean noParamChangeCheck(Expense oldExpense, Optional<String> newTitle,
                                       Optional<BigDecimal> newAmount,
                                       Optional<ExpenseType> newExpenseType,
                                       Optional<String> newDescription
    ) {
        if (newTitle.isPresent() && !oldExpense.expenseDetails().title().equals(newTitle.get())) return false;
        if (newAmount.isPresent() && !oldExpense.expenseDetails().amount().equals(newAmount.get())) return false;
        if (newExpenseType.isPresent() && !oldExpense.expenseDetails().expenseType().equals(newExpenseType.get()))
            return false;
        if (newDescription.isPresent() && !oldExpense.expenseDetails().description().equals(newDescription.get()))
            return false;

        return true;
    }

    private void validationExpenseAmount(BigDecimal amount, BudgetIdWrapper budgetId) {
        budgetRepository.findById(budgetId).ifPresent((budget) ->
                                                      {
                                                          checkBudgetLimit(amount, budget);
                                                          singleMaxExpValidation(amount, budget);
                                                      });
    }

    private void updateHistoryChange(Expense oldExpense) {
        TreeMap<Integer, LocalDateTime> history = oldExpense.expenseDetails().historyOfChanges();
        Integer newRecordNumber = history.lastEntry().getKey() + 1;
        history.put(newRecordNumber, LocalDateTime.now());
    }

    private String duplicateExpenseTitleCheck(String title, BudgetIdWrapper budgetId) {
        if (expenseRepository.existsByBudgetIdAndExpenseDetails_Title(budgetId, title)) {
            long counter = 0;
            StringBuilder stringBuilder = new StringBuilder(title);
            while (expenseRepository.existsByBudgetIdAndExpenseDetails_Title(budgetId, stringBuilder.toString())) {
                counter++;
                stringBuilder = new StringBuilder(title);
                stringBuilder.append("(").append(counter).append(")");
            }
            return stringBuilder.toString();
        }
        return title;
    }

    private void singleMaxExpValidation(BigDecimal amount, Budget budget) {
        if (budget.budgetDetails().maxSingleExpense().compareTo(amount) < 0) {
            throw new ExpenseTooBigException("Expense exceed single maximal expense amount in the budget!");
        }
    }

    private void checkBudgetLimit(BigDecimal amount, Budget budget) {
        if (budget.budgetDetails().budgetType().getValue().compareTo(BigDecimal.valueOf(0)) < 0) {
            return;
        }

        BigDecimal totalBudgetLimit = budget.budgetDetails().limit()
                                            .multiply(budget.budgetDetails().budgetType().getValue());

        BigDecimal totalExpensesSum = expenseRepository.findAllByBudgetIdAndUserId(
                                                               budget.budgetId(), budget.userId())
                                                       .stream()
                                                       .map(Expense::expenseDetails)
                                                       .map(ExpenseDetails::amount)
                                                       .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal amountLeft = totalBudgetLimit.subtract(totalExpensesSum);

        if (amountLeft.compareTo(amount) < 0) {
            throw new ExpenseTooBigException("Expense exceed the budget limit!");
        }
    }
}