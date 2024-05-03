package com.example.final_project.domain.budgets.appusers.service;

import com.example.final_project.domain.budgets.appusers.Budget;
import com.example.final_project.domain.budgets.appusers.BudgetPeriod;
import com.example.final_project.domain.budgets.appusers.BudgetType;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseType;
import com.example.final_project.domain.users.appusers.UserIdWrapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

public interface BudgetServiceInnerLogic {
    String duplicateBudgetTitleCheck(String title, UserIdWrapper userId);

    void updateHistoryChange(Budget oldBudget);

    BudgetPeriod getBudgetPeriod(LocalDate startTime, LocalDate endTime);

    BigDecimal totalExpensesValueSum(Budget budget);

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
                               BudgetPeriod newBudgetPeriod,
                               Optional<String> newDescription
    );
}
