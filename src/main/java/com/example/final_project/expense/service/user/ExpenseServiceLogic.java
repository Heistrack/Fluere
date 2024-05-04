package com.example.final_project.expense.service.user;

import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.budget.model.MKTCurrency;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseType;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExpenseServiceLogic {
    void balanceUpdate(MKTCurrency currency, BigDecimal amount, Expense oldExpense);

    void updateHistoryChange(Expense oldExpense);

    boolean noParamChangeCheck(Expense oldExpense,
                               Optional<String> newTitle,
                               Optional<BigDecimal> newAmount,
                               Optional<MKTCurrency> newCurrency,
                               Optional<ExpenseType> newExpenseType,
                               Optional<String> newDescription
    );

    void validationExpenseAmount(MKTCurrency currency, BigDecimal amount, BudgetIdWrapper budgetId);

    String duplicateExpenseTitleCheck(String title, BudgetIdWrapper budgetId);

    void singleMaxExpValidation(MKTCurrency currency, BigDecimal amount, Budget budget);

    void checkBudgetLimit(MKTCurrency currency, BigDecimal amount, Budget budget);
}
