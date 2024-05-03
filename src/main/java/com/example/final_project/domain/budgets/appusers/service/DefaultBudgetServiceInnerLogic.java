package com.example.final_project.domain.budgets.appusers.service;

import com.example.final_project.domain.budgets.appusers.Budget;
import com.example.final_project.domain.budgets.appusers.BudgetDetails;
import com.example.final_project.domain.budgets.appusers.BudgetPeriod;
import com.example.final_project.domain.budgets.appusers.BudgetType;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseDetails;
import com.example.final_project.domain.expenses.ExpenseType;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
import com.example.final_project.infrastructure.exprepo.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultBudgetServiceInnerLogic implements BudgetServiceInnerLogic {
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public String duplicateBudgetTitleCheck(String title, UserIdWrapper userId) {
        if (budgetRepository.existsByUserIdAndBudgetDetails_Title(userId, title)) {
            long counter = 0;
            StringBuilder stringBuilder = new StringBuilder(title);
            while (budgetRepository.existsByUserIdAndBudgetDetails_Title(userId, stringBuilder.toString())) {
                counter++;
                stringBuilder = new StringBuilder(title);
                stringBuilder.append("(").append(counter).append(")");
            }
            return stringBuilder.toString();
        }
        return title;
    }

    public void updateHistoryChange(Budget oldBudget) {
        TreeMap<Integer, LocalDateTime> history = oldBudget.budgetDetails().historyOfChanges();
        Integer newRecordNumber = history.lastEntry().getKey() + 1;
        history.put(newRecordNumber, LocalDateTime.now());
    }

    public BudgetPeriod getBudgetPeriod(LocalDate startTime, LocalDate endTime) {
        if (startTime == null) {
            LocalDate now = LocalDate.now();
            startTime = LocalDate.of(now.getYear(), now.getMonth().getValue(), 1);
        }
        if (endTime == null) {
            LocalDate now = LocalDate.now();
            endTime = LocalDate.of(now.getYear(), now.getMonth().getValue(), now.lengthOfMonth());
        }

        return BudgetPeriod.newOf(startTime, endTime);
    }

    public BigDecimal totalExpensesValueSum(Budget budget) {
        return expenseRepository.findAllByBudgetId(budget.budgetId())
                                .stream()
                                .map(Expense::expenseDetails)
                                .map(ExpenseDetails::amount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public TreeMap<LocalDate, List<Expense>> getExpensesByDay(List<Expense> expenses) {
        return expenses.stream().collect(Collectors.groupingBy(
                exp -> exp.expenseDetails()
                          .historyOfChanges()
                          .firstEntry()
                          .getValue()
                          .toLocalDate(), TreeMap::new, Collectors.toList()));
    }

    public HashMap<ExpenseType, List<Expense>> getExpensesByCategory(List<Expense> expenses) {
        return expenses.stream().collect(
                Collectors.groupingBy(exp -> exp.expenseDetails().expenseType(), HashMap::new, Collectors.toList()));
    }

    public HashMap<ExpenseType, Float> getExpenseCategoryPercentage(List<Expense> expenses, Budget budget) {
        HashMap<ExpenseType, List<Expense>> expensesByCategory = getExpensesByCategory(expenses);
        BigDecimal limit = budget.budgetDetails().limit();

        return new HashMap<>(expensesByCategory
                                     .entrySet()
                                     .stream()
                                     .collect(Collectors
                                                      .toMap(
                                                              Map.Entry::getKey,
                                                              val -> budgetFullFillPercentage(limit, val.getValue()
                                                                                                        .stream()
                                                                                                        .map(exp -> exp
                                                                                                                .expenseDetails()
                                                                                                                .amount())
                                                                                                        .reduce(
                                                                                                                BigDecimal.ZERO,
                                                                                                                BigDecimal::add
                                                                                                        )
                                                              )
                                                      )));
    }

    public Float budgetFullFillPercentage(BigDecimal base, BigDecimal actual) {
        return actual.multiply(BigDecimal.valueOf(100)).divide(base, 1, RoundingMode.DOWN).floatValue();
    }

    public String getTrueLimitFromBudget(Budget budget) {
        BudgetType ourBudgetType = budget.budgetDetails().budgetType();
        BigDecimal limit = budget.budgetDetails().limit().multiply(ourBudgetType.getValue());
        if (!ourBudgetType.getValue().equals(BigDecimal.valueOf(-1))) {
            return limit.toString();
        } else {
            return "no limit";
        }
    }

    public boolean noParamChangeCheck(Budget oldBudget,
                                      Optional<String> newTitle,
                                      Optional<BigDecimal> newLimit,
                                      Optional<BudgetType> newBudgetType,
                                      Optional<BigDecimal> newMaxSingleExpense,
                                      BudgetPeriod newBudgetPeriod,
                                      Optional<String> newDescription
    ) {
        BudgetDetails oldBudgetDetails = oldBudget.budgetDetails();
        if (newTitle.isPresent() && !oldBudgetDetails.title().equals(newTitle.get())) return false;
        if (newLimit.isPresent() && !oldBudgetDetails.limit().equals(newLimit.get())) return false;
        if (newBudgetType.isPresent() && !oldBudgetDetails.budgetType().equals(newBudgetType.get()))
            return false;
        if (newMaxSingleExpense.isPresent() && !oldBudgetDetails.maxSingleExpense().equals(newMaxSingleExpense.get()))
            return false;
        if (newDescription.isPresent() && !oldBudgetDetails.description().equals(newDescription.get()))
            return false;
        if (!oldBudgetDetails.budgetPeriod().equals(newBudgetPeriod))
            return false;

        return true;
    }
}
