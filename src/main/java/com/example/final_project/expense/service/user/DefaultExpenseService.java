package com.example.final_project.expense.service.user;

import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.budget.repository.BudgetRepository;
import com.example.final_project.currencyapi.model.MKTCurrency;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseDetails;
import com.example.final_project.expense.model.ExpenseIdWrapper;
import com.example.final_project.expense.model.ExpenseType;
import com.example.final_project.expense.repository.ExpenseRepository;
import com.example.final_project.security.service.JwtService;
import com.example.final_project.userentity.model.UserIdWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DefaultExpenseService implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final Supplier<ExpenseIdWrapper> expenseIdSupplier;
    private final ExpenseServiceLogic innerServiceLogic;
    private final JwtService jwtService;
    //TODO remove
    private final BudgetRepository bd;

    @Override
    public Expense registerNewExpense(BudgetIdWrapper budgetId, String title, BigDecimal amount, MKTCurrency currency,
                                      ExpenseType expenseType,
                                      String description,
                                      Authentication authentication
    ) {
        //TODO expense's title could be duplicated after all
        String checkedTitle = innerServiceLogic.duplicateExpenseTitleCheck(title, budgetId);
        innerServiceLogic.validationExpenseAmount(currency, amount, budgetId);

        TreeMap<Integer, LocalDateTime> historyOfChange = new TreeMap<>();
        historyOfChange.put(1, LocalDateTime.now());

        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Expense expense = Expense.newOf(
                expenseIdSupplier.get(), budgetId, userId, ExpenseDetails.newOf(checkedTitle, amount,
                                                                                currency,
                                                                                historyOfChange,
                                                                                expenseType,
                                                                                description == null ? "" : description
                ));
        innerServiceLogic.addBalance(currency, amount, budgetId);
        //TODO remove lines below
        log.warn("xD1");
        log.warn(bd.findById(budgetId).orElseThrow().budgetDetails().expenseSet().toString());
        log.warn("xD2");
        return expenseRepository.save(expense);
    }

    @Override
    public Expense getExpenseById(ExpenseIdWrapper expenseId, Authentication authentication) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        return expenseRepository.findByExpenseIdAndUserId(expenseId, userId)
                                .orElseThrow(() -> new NoSuchElementException("Expense doesn't exist."));
    }

    @Override
    public Page<Expense> getAllByPage(Pageable pageable, Authentication authentication) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        return expenseRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Page<Expense> getAllExpensesByBudgetId(BudgetIdWrapper budgetId,
                                                  Pageable pageable, Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        return expenseRepository.findAllByBudgetIdAndUserId(budgetId, userId, pageable);
    }

    @Override
    public Expense updateExpenseById(
            ExpenseIdWrapper expenseId,
            String title,
            BigDecimal amount,
            MKTCurrency currency,
            ExpenseType expenseType,
            String description,
            Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Expense oldExpense = expenseRepository.findByExpenseIdAndUserId(expenseId, userId).orElseThrow(
                () -> new NoSuchElementException("Can't update expense, because it doesn't exist"));

        if (innerServiceLogic.noParamChangeCheck(oldExpense, Optional.of(title),
                                                 Optional.of(amount),
                                                 Optional.of(currency),
                                                 Optional.ofNullable(expenseType),
                                                 Optional.ofNullable(description)
        )) {
            return oldExpense;
        }
        if (!title.equals(oldExpense.expenseDetails().title())) {
            title = innerServiceLogic.duplicateExpenseTitleCheck(title, oldExpense.budgetId());
        }

        if (!amount.equals(oldExpense.expenseDetails().amount()) || !currency.equals(
                oldExpense.expenseDetails().currency())) {
            innerServiceLogic.validationExpenseAmount(currency, amount, oldExpense.budgetId());
            innerServiceLogic.balanceUpdate(currency, amount, oldExpense);
        }

        innerServiceLogic.updateHistoryChange(oldExpense);

        return expenseRepository.save(Expense.newOf(
                expenseId,
                oldExpense.budgetId(),
                userId,
                ExpenseDetails.newOf(
                        title,
                        amount,
                        currency,
                        oldExpense.expenseDetails().historyOfChanges(),
                        expenseType == null ? ExpenseType.NO_CATEGORY : expenseType,
                        description == null ? "" : description
                )
        ));
    }


    @Override
    public Expense patchExpenseContent(
            ExpenseIdWrapper expenseId,
            Optional<String> title,
            Optional<BigDecimal> amount,
            Optional<MKTCurrency> currency,
            Optional<ExpenseType> expenseType,
            Optional<String> description,
            Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Expense oldExpense = expenseRepository.findByExpenseIdAndUserId(expenseId, userId)
                                              .orElseThrow(() -> new NoSuchElementException(
                                                      "There's no such expense."));

        if (innerServiceLogic.noParamChangeCheck(oldExpense, title, amount, currency, expenseType, description)) {
            return oldExpense;
        }
        if (title.isPresent() && !title.get().equals(oldExpense.expenseDetails().title())) {
            title = Optional.of(innerServiceLogic.duplicateExpenseTitleCheck(title.get(), oldExpense.budgetId()));
        }

        MKTCurrency checkedCurrency = currency.orElse(oldExpense.expenseDetails().currency());
        BigDecimal checkedAmount = amount.orElse(oldExpense.expenseDetails().amount());

        if (!checkedAmount.equals(oldExpense.expenseDetails().amount()) || !checkedCurrency.equals(
                oldExpense.expenseDetails().currency())) {
            innerServiceLogic.validationExpenseAmount(checkedCurrency, checkedAmount, oldExpense.budgetId());
            innerServiceLogic.balanceUpdate(checkedCurrency, checkedAmount, oldExpense);
        }

        innerServiceLogic.updateHistoryChange(oldExpense);
        Optional<String> checkedTitle = title;

        return expenseRepository.save(expenseRepository.findByExpenseIdAndUserId(expenseId, userId).map(
                expenseFromRepository -> Expense.newOf(
                        expenseId,
                        oldExpense.budgetId(),
                        userId,
                        ExpenseDetails.newOf(
                                checkedTitle.orElseGet(() -> expenseFromRepository.expenseDetails().title()),
                                checkedAmount,
                                checkedCurrency,
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
}
