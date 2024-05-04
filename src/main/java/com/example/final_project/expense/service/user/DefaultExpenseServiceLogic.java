package com.example.final_project.expense.service.user;


import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.budget.model.MKTCurrency;
import com.example.final_project.budget.repository.BudgetRepository;
import com.example.final_project.exception.custom.ExpenseTooBigException;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseType;
import com.example.final_project.expense.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class DefaultExpenseServiceLogic implements ExpenseServiceLogic {
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;


    public void balanceUpdate(MKTCurrency currency, BigDecimal amount, Expense oldExpense) {
        Budget budget = budgetRepository.findById(oldExpense.budgetId())
                                        .orElseThrow(() -> new NoSuchElementException("Budget not found."));
        budget.subtractBalance(oldExpense.expenseDetails().currency(), oldExpense.expenseDetails().amount());
        budget.addBalance(currency, amount);
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

    public String duplicateExpenseTitleCheck(String title, BudgetIdWrapper budgetId) {
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

    //TODO provide common inner logic class for both expenses controller.
    public void singleMaxExpValidation(MKTCurrency currency, BigDecimal amount, Budget budget) {
        if (budget.budgetDetails().maxSingleExpense().compareTo(amount.multiply(currency.getValue())) < 0) {
            throw new ExpenseTooBigException("Expense exceed single maximal expense amount in the budget!");
        }
    }

    public void checkBudgetLimit(MKTCurrency currency, BigDecimal amount, Budget budget) {
        if (budget.budgetDetails().budgetType().getValue().compareTo(BigDecimal.valueOf(0)) < 0) {
            return;
        }
        //TODO should have an option or take a defeault budget currency, then calculate expense currency and then check, right now is hardcoded to PLN
        BigDecimal totalBudgetLimit = budget.budgetDetails().limit()
                                            .multiply(budget.budgetDetails().budgetType().getValue());
        BigDecimal totalExpensesSum = budget.showBalance(currency);
        BigDecimal realAmount = amount.multiply(currency.getValue());
        BigDecimal amountLeft = totalBudgetLimit.subtract(totalExpensesSum);

        if (amountLeft.compareTo(realAmount) < 0) {
            throw new ExpenseTooBigException("Expense exceed the budget limit!");
        }
    }
}
