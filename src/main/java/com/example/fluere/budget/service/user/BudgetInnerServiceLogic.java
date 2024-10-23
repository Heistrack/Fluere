package com.example.fluere.budget.service.user;

import com.example.fluere.budget.model.Budget;
import com.example.fluere.budget.model.BudgetPeriod;
import com.example.fluere.budget.model.BudgetType;
import com.example.fluere.budget.model.LinkableDTO;
import com.example.fluere.budget.response.BudgetUserMoneySavedDTO;
import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.expense.model.Expense;
import com.example.fluere.expense.model.ExpenseType;
import com.example.fluere.userentity.model.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

public interface BudgetInnerServiceLogic {
    String duplicateBudgetTitleCheck(String title, UserIdWrapper userId);

    void updateHistoryChange(Budget oldBudget);

    BudgetPeriod getBudgetPeriod(LocalDate startTime, LocalDate endTime);

    TreeMap<LocalDate, List<Expense>> getExpensesByDay(List<Expense> expenses);

    HashMap<ExpenseType, List<Expense>> getExpensesByCategory(List<Expense> expenses);

    HashMap<ExpenseType, Float> getExpenseCategoryPercentage(List<Expense> expenses, Budget budget);

    Float budgetFullFillPercentage(BigDecimal base, BigDecimal actual);

    String getTrueLimitFromBudget(Budget budget);

    BudgetUserMoneySavedDTO getMoneySavedBySingleUser(UserIdWrapper userId);

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

    <T extends LinkableDTO> PagedModel<T> getPagedModel(Page<T> linkableDTOs, Class<T> classCast,
                                                        Class<?> controllerClass
    );
}
