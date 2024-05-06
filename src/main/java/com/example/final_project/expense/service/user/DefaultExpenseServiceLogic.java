package com.example.final_project.expense.service.user;


import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.budget.repository.BudgetRepository;
import com.example.final_project.currencyapi.model.MKTCurrency;
import com.example.final_project.currencyapi.repository.CurrencyRepository;
import com.example.final_project.exception.custom.ExpenseTooBigException;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseType;
import com.example.final_project.expense.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DefaultExpenseServiceLogic implements ExpenseServiceLogic {
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final CurrencyRepository currencyRepository;


    public void balanceUpdate(MKTCurrency currency, BigDecimal amount, Expense oldExpense) {
        Budget budget = budgetRepository.findById(oldExpense.budgetId())
                                        .orElseThrow(() -> new NoSuchElementException("Budget not found."));
        budget.subtractBalance(oldExpense.expenseDetails().currency(), oldExpense.expenseDetails().amount());
        budget.addBalance(currency, amount);
        budgetRepository.save(budget);
    }

    public void addBalance(MKTCurrency currency, BigDecimal amount, BudgetIdWrapper budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                                        .orElseThrow(() -> new NoSuchElementException("Budget not found."));
        budget.addBalance(currency, amount);
        budgetRepository.save(budget);
    }


    public void updateHistoryChange(Expense oldExpense) {
        TreeMap<Integer, LocalDateTime> history = oldExpense.expenseDetails().historyOfChanges();
        Integer newRecordNumber = history.lastEntry().getKey() + 1;
        history.put(newRecordNumber, LocalDateTime.now());
    }

    public boolean noParamChangeCheck(Expense oldExpense,
                                      Optional<String> newTitle,
                                      Optional<BigDecimal> newAmount,
                                      Optional<MKTCurrency> newCurrency,
                                      Optional<ExpenseType> newExpenseType,
                                      Optional<String> newDescription
    ) {
        if (newTitle.isPresent() && !oldExpense.expenseDetails().title().equals(newTitle.get())) return false;
        if (newAmount.isPresent() && !oldExpense.expenseDetails().amount().equals(newAmount.get())) return false;
        if (newCurrency.isPresent() && !oldExpense.expenseDetails().currency().equals(newCurrency.get())) return false;
        if (newExpenseType.isPresent() && !oldExpense.expenseDetails().expenseType().equals(newExpenseType.get()))
            return false;
        if (newDescription.isPresent() && !oldExpense.expenseDetails().description().equals(newDescription.get()))
            return false;

        return true;
    }

    public void validationExpenseAmount(MKTCurrency currency, BigDecimal amount, BudgetIdWrapper budgetId) {
        budgetRepository.findById(budgetId).ifPresent((budget) ->
                                                      {
                                                          checkBudgetLimit(currency, amount, budget);
                                                          singleMaxExpValidation(currency, amount, budget);
                                                      });
    }

    public void singleMaxExpValidation(MKTCurrency expenseCurrency, BigDecimal amount, Budget budget) {
        MKTCurrency budgetCurrency = budget.budgetDetails().defaultCurrency();
        if (budget.budgetDetails().maxSingleExpense().compareTo(amount.multiply(getConversionCurrencyRatio(
                expenseCurrency,
                budgetCurrency
        ))) < 0) {
            throw new ExpenseTooBigException("Expense exceed single maximal expense amount in the budget!");
        }
    }

    public void checkBudgetLimit(MKTCurrency expenseCurrency, BigDecimal amount, Budget budget) {
        //TODO check how limit is made methods
        if (budget.budgetDetails().limit().compareTo(BigDecimal.valueOf(0)) < 0) {
            return;
        }
        //TODO check budget for automatic balance validation
        //TODO should have an option or take a defeault budget currency, then calculate expense currency and then check, right now is hardcoded to PLN
        BigDecimal totalBudgetLimit = budget.budgetDetails().limit()
                                            .multiply(budget.budgetDetails().limit());
        //TODO add showBalance in the service
        BigDecimal totalExpensesSum = sumAllExpensesByCurrency(budget.budgetDetails().defaultCurrency(), budget);
        BigDecimal realAmount = amount.multiply(getConversionCurrencyRatio(expenseCurrency, budget.budgetDetails()
                                                                                                  .defaultCurrency()));
        BigDecimal amountLeft = totalBudgetLimit.subtract(totalExpensesSum);

        if (amountLeft.compareTo(realAmount) < 0) {
            throw new ExpenseTooBigException("Expense exceed the budget limit!");
        }
    }

    public BigDecimal sumAllExpensesByCurrency(MKTCurrency expectedCurrency, Budget budget) {
        HashMap<MKTCurrency, BigDecimal> allExpenses = budget.budgetDetails().expenseSet()
                                                             .expenseMap();
        BigDecimal balance = BigDecimal.ZERO;
        for (Map.Entry<MKTCurrency, BigDecimal> expensePair : allExpenses.entrySet()) {
            balance = balance.add(expensePair.getValue().multiply(
                    getConversionCurrencyRatio(expensePair.getKey(), expectedCurrency)));
        }
        return balance;
    }

    public BigDecimal getConversionCurrencyRatio(MKTCurrency expenseCurrency, MKTCurrency expectedCurrency) {
        if (expenseCurrency.equals(expectedCurrency)) {
            return BigDecimal.ONE;
        }
        HashMap<MKTCurrency, BigDecimal> conversionRates = currencyRepository.findAll().getFirst().conversionRates();
//TODO check budget currency change and how it works with expense validation
        BigDecimal expenseCurrencyToUSDRatio = BigDecimal.ONE;
        if (!expenseCurrency.equals(MKTCurrency.USD)) {
            expenseCurrencyToUSDRatio = conversionRates.get(expenseCurrency);
        }

        BigDecimal defaultCurrencyToUSDRatio = BigDecimal.ONE;
        if (!expectedCurrency.equals(MKTCurrency.USD)) {
            defaultCurrencyToUSDRatio = conversionRates.get(expectedCurrency);
        }

        return defaultCurrencyToUSDRatio.divide(expenseCurrencyToUSDRatio, 4, RoundingMode.HALF_UP);
    }
}
