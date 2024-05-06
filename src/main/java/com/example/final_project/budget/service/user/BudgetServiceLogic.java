package com.example.final_project.budget.service.user;

import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.BudgetPeriod;
import com.example.final_project.budget.model.BudgetType;
import com.example.final_project.currencyapi.model.MKTCurrency;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseType;
import com.example.final_project.userentity.model.UserIdWrapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

public interface BudgetServiceLogic {
    String duplicateBudgetTitleCheck(String title, UserIdWrapper userId);

    void updateHistoryChange(Budget oldBudget);

    BudgetPeriod getBudgetPeriod(LocalDate startTime, LocalDate endTime);

    TreeMap<LocalDate, List<Expense>> getExpensesByDay(List<Expense> expenses);

    HashMap<ExpenseType, List<Expense>> getExpensesByCategory(List<Expense> expenses);

    HashMap<ExpenseType, Float> getExpenseCategoryPercentage(List<Expense> expenses, Budget budget);

    Float budgetFullFillPercentage(BigDecimal base, BigDecimal actual);

    String getTrueLimitFromBudget(Budget budget);

    boolean noParamChangeCheck(Budget oldBudget,
                               Optional<String> newTitle,
                               Optional<BigDecimal> newLimit,
                               Optional<BudgetType> newBudgetType,
                               Optional<BigDecimal> newMaxSingleExpense,
                               Optional<MKTCurrency> newDefaultCurrency,
                               BudgetPeriod newBudgetPeriod,
                               Optional<String> newDescription
    );

    BigDecimal showBalanceByCurrency(MKTCurrency expectedCurrency, Budget budget);
}
