package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.appusers.Budget;
import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import com.example.final_project.domain.expenses.exceptions.ExpenseTooBigException;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
import com.example.final_project.infrastructure.exprepo.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class DefaultExpenseService implements ExpensesService {

    private final ExpenseRepository expenseRepository;
    private final Supplier<ExpenseIdWrapper> expenseIdSupplier;
    private final BudgetRepository budgetRepository;
    private final JwtService jwtService;

    @Override
    public Expense registerNewExpense(String title, BigDecimal amount, BudgetIdWrapper budgetId,
                                      Authentication authentication,
                                      ExpenseType expenseType,
                                      String description
    ) {
        String checkedTitle = duplicateExpenseTitleCheck(title, budgetId);
        validationExpenseAmount(amount, budgetId);

        TreeMap<Integer, LocalDateTime> historyOfChange = new TreeMap<>();
        historyOfChange.put(1, LocalDateTime.now());

        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Expense expense = Expense.newOf(
                expenseIdSupplier.get(), budgetId, userId, ExpenseDetails.newOf(checkedTitle, amount,
                                                                                historyOfChange,
                                                                                expenseType,
                                                                                description == null ? "" : description
                ));
        return expenseRepository.save(expense);
    }

    @Override
    public Expense getExpenseById(ExpenseIdWrapper expenseId, Authentication authentication) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        return expenseRepository.findByExpenseIdAndUserId(expenseId, userId)
                                .orElseThrow(() -> new NoSuchElementException("Expense doesn't exist."));
    }

    @Override
    public Page<Expense> getAllByPage(Authentication authentication, Pageable pageable) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        return expenseRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Page<Expense> getAllExpensesByBudgetId(Authentication authentication, BudgetIdWrapper budgetId,
                                                  Pageable pageable
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        return expenseRepository.findAllByBudgetIdAndUserId(budgetId, userId, pageable);
    }

    @Override
    public Expense updateExpenseById(
            ExpenseIdWrapper expenseId,
            String title,
            BigDecimal amount,
            Authentication authentication,
            Optional<ExpenseType> expenseType,
            Optional<String> description
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Expense oldExpense = expenseRepository.findByExpenseIdAndUserId(expenseId, userId).orElseThrow(
                () -> new NoSuchElementException("Can't update expense, because it doesn't exist"));

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
                userId,
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
            Authentication authentication,
            Optional<ExpenseType> expenseType,
            Optional<String> description
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Expense oldExpense = expenseRepository.findByExpenseIdAndUserId(expenseId, userId)
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

        return expenseRepository.save(expenseRepository.findByExpenseIdAndUserId(expenseId, userId).map(
                expenseFromRepository -> Expense.newOf(
                        expenseId,
                        oldExpense.budgetId(),
                        userId,
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
    public void deleteExpenseById(ExpenseIdWrapper expenseId, Authentication authentication) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        expenseRepository.deleteByExpenseIdAndUserId(expenseId, userId);
    }


    //TODO poprawić service wyydatków zwykły i admina + poprawić serwis budzetowy zwykly i admina

    private void updateHistoryChange(Expense oldExpense) {
        TreeMap<Integer, LocalDateTime> history = oldExpense.expenseDetails().historyOfChanges();
        Integer newRecordNumber = history.lastEntry().getKey() + 1;
        history.put(newRecordNumber, LocalDateTime.now());
    }


    private boolean noParamChangeCheck(Expense oldExpense,
                                       Optional<String> newTitle,
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
