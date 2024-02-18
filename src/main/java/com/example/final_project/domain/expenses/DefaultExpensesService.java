package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetIdWrapper;
import com.example.final_project.domain.expenses.exceptions.ExpenseTooBigException;
import com.example.final_project.domain.users.UserIdWrapper;
import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
import com.example.final_project.infrastructure.exprepo.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j //TODO remove logs
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
                                      TypeOfExpense typeOfExpense
    ) {
        title = duplicateExpenseTitleCheck(title, budgetId);
        validationExpenseAmount(amount, budgetId);

        TreeMap<Integer, LocalDateTime> historyOfChange = new TreeMap<>();
        historyOfChange.put(1, LocalDateTime.now());

        Expense expense = Expense.newOf(expenseIdSupplier.get(), budgetId, userId, ExpenseDetails.newOf(title, amount,
                                                                                                        historyOfChange,
                                                                                                        typeOfExpense
        ));
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
        Expense oldExpense = expenseRepository.findByExpenseIdAndUserId(expenseId, userId)
                                              .orElseThrow(() -> new NoSuchElementException(
                                                      "There's no such expense."));


        if (Objects.equals(oldExpense.expenseDetails().title(), title.get()) &&
                Objects.equals(oldExpense.expenseDetails().typeOfExpense(), typeOfExpense.get()) &&
                Objects.equals(oldExpense.expenseDetails().amount(), amount.get())) return oldExpense;

        Map<String, Object> stringObjectMap = validationForNewExpense(
                oldExpense,
                title.orElse(oldExpense.expenseDetails().title()),
                amount.orElse(oldExpense.expenseDetails().amount())
        );

        Optional<String> checkedTitle = Optional.ofNullable((String) stringObjectMap.get("checkedTitle"));

        return expenseRepository.save(expenseRepository.findByExpenseIdAndUserId(expenseId, userId).map(
                expenseFromRepository -> Expense.newOf(
                        expenseId,
                        oldExpense.budgetId(),
                        userId,
                        ExpenseDetails.newOf(
                                checkedTitle.orElseGet(() -> expenseFromRepository.expenseDetails().title()),
                                amount.orElseGet(() -> expenseFromRepository.expenseDetails().amount()),
                                oldExpense.expenseDetails().historyOfChanges(),
                                typeOfExpense.orElseGet(() -> expenseFromRepository.expenseDetails().typeOfExpense())
                        )
                )
        ).orElseThrow(IllegalArgumentException::new));
    }

    @Override
    public Expense updateExpenseById(
            ExpenseIdWrapper expenseId,
            String title,
            BigDecimal amount,
            UserIdWrapper userId,
            Optional<TypeOfExpense> typeOfExpense
    ) {
        Expense oldExpense = expenseRepository.findByExpenseIdAndUserId(expenseId, userId).orElseThrow(
                () -> new NoSuchElementException("Can't update expense, because it doesn't exist"));

        if (Objects.equals(oldExpense.expenseDetails().title(), title) &&
                Objects.equals(oldExpense.expenseDetails().typeOfExpense(), typeOfExpense.get()) &&
                Objects.equals(oldExpense.expenseDetails().amount(), amount)) return oldExpense;

        Map<String, Object> validatedValues = validationForNewExpense(oldExpense, title, amount);

        return expenseRepository.save(Expense.newOf(
                expenseId,
                oldExpense.budgetId(),
                userId,
                ExpenseDetails.newOf(
                        (String) validatedValues.get("checkedTitle"),
                        amount,
                        oldExpense.expenseDetails().historyOfChanges(),
                        typeOfExpense.orElse(TypeOfExpense.NO_CATEGORY)
                )
        ));
    }

    private Map<String, Object> validationForNewExpense(Expense oldExpense,
                                                        String title,
                                                        BigDecimal amount
    ) {
        ExpenseDetails oldExpenseDetails = oldExpense.expenseDetails();
        BudgetIdWrapper budgetId = oldExpense.budgetId();

        if (!amount.equals(oldExpense.expenseDetails().amount())) {
            validationExpenseAmount(amount, budgetId);
        }

        HashMap<String, Object> returnMap = new HashMap<>();

        if (!title.equals(oldExpenseDetails.title())) {
            returnMap.put("checkedTitle", duplicateExpenseTitleCheck(title, budgetId));
        }

        Integer newRecordNumber = oldExpenseDetails.historyOfChanges().lastEntry().getKey() + 1;
        oldExpenseDetails.historyOfChanges().put(newRecordNumber, LocalDateTime.now());

        return returnMap;
    }

    private void validationExpenseAmount(BigDecimal amount, BudgetIdWrapper budgetId) {
        budgetRepository.findById(budgetId).ifPresent((budget) ->
                                                      {
                                                          checkBudgetLimit(amount, budget);
                                                          singleMaxExpValidation(amount, budget);
                                                      });
    }

    @Override
    public Page<Expense> findAllExpensesByBudgetId(UserIdWrapper userId, BudgetIdWrapper budgetId, Pageable pageable) {
        return expenseRepository.findAllByBudgetIdAndUserId(budgetId, userId, pageable);
    }

    @Override
    public Page<Expense> findAllByPage(UserIdWrapper userId, Pageable pageable) {
        return expenseRepository.findAllByUserId(userId, pageable);
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
                                                       .map(Expense::expenseDetails)
                                                       .map(ExpenseDetails::amount)
                                                       .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal amountLeft = totalBudgetLimit.subtract(totalExpensesSum);

        if (amountLeft.compareTo(amount) < 0) {
            throw new ExpenseTooBigException("Expense exceed the budget limit!");
        }
    }
}
