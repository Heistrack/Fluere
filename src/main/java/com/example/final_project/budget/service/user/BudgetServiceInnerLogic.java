package com.example.final_project.budget.service.user;

import com.example.final_project.budget.service.Budget;
import com.example.final_project.budget.service.BudgetPeriod;
import com.example.final_project.budget.service.BudgetType;
import com.example.final_project.expense.service.Expense;
import com.example.final_project.expense.service.ExpenseType;
import com.example.final_project.userentity.service.UserIdWrapper;

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
