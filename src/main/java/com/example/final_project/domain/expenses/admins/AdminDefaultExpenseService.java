package com.example.final_project.domain.expenses.admins;

import com.example.final_project.domain.budgets.appusers.Budget;
import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseDetails;
import com.example.final_project.domain.expenses.ExpenseIdWrapper;
import com.example.final_project.domain.expenses.ExpenseType;
import com.example.final_project.domain.expenses.exceptions.ExpenseTooBigException;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
import com.example.final_project.infrastructure.exprepo.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
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

        Map<String, Object> validatedValues = validationForNewExpense(oldExpense, title, amount);

        return expenseRepository.save(Expense.newOf(
                expenseId,
                oldExpense.budgetId(),
                oldExpense.userId(),
                ExpenseDetails.newOf(
                        (String) validatedValues.get("checkedTitle"),
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

        Map<String, Object> stringObjectMap = validationForNewExpense(
                oldExpense,
                title.orElse(oldExpense.expenseDetails().title()),
                amount.orElse(oldExpense.expenseDetails().amount())
        );

        Optional<String> checkedTitle = Optional.ofNullable((String) stringObjectMap.get("checkedTitle"));

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
        } else {
            returnMap.put("checkedTitle", title);
        }

        Integer newRecordNumber = oldExpenseDetails.historyOfChanges().lastEntry().getKey() + 1;
        oldExpenseDetails.historyOfChanges().put(newRecordNumber, LocalDateTime.now());

        return returnMap;
    }

    private boolean noParamChangeCheck(Expense oldExpense, Optional<String> newTitle,
                                       Optional<BigDecimal> newAmount,
                                       Optional<ExpenseType> newExpenseType,
                                       Optional<String> newDescription
    ) {
        if(newTitle.isPresent() && !oldExpense.expenseDetails().title().equals(newTitle.get())) return false;
        if(newAmount.isPresent() && !oldExpense.expenseDetails().amount().equals(newAmount.get())) return false;
        if(newExpenseType.isPresent() && !oldExpense.expenseDetails().expenseType().equals(newExpenseType.get())) return false;
        if(newDescription.isPresent() && !oldExpense.expenseDetails().description().equals(newDescription.get())) return false;

        return true;
    }

    private void validationExpenseAmount(BigDecimal amount, BudgetIdWrapper budgetId) {
        budgetRepository.findById(budgetId).ifPresent((budget) ->
                                                      {
                                                          checkBudgetLimit(amount, budget);
                                                          singleMaxExpValidation(amount, budget);
                                                      });
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