package com.example.final_project.expense.service.user;

import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.currencyapi.model.MKTCurrency;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseType;
import com.example.final_project.expense.response.ExpenseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExpenseServiceLogic {
    void balanceUpdate(MKTCurrency currency, BigDecimal amount, Expense oldExpense);

    void addBalance(MKTCurrency currency, BigDecimal amount, BudgetIdWrapper budgetId);

    void updateHistoryChange(Expense oldExpense);

    boolean noParamChangeCheck(Expense oldExpense,
                               Optional<String> newTitle,
                               Optional<BigDecimal> newAmount,
                               Optional<MKTCurrency> newCurrency,
                               Optional<ExpenseType> newExpenseType,
                               Optional<String> newDescription
    );

    void validationExpenseAmount(MKTCurrency currency, BigDecimal amount, BudgetIdWrapper budgetId);

    void singleMaxExpValidation(MKTCurrency currency, BigDecimal amount, Budget budget);

    void checkBudgetLimit(MKTCurrency currency, BigDecimal amount, Budget budget);

    BigDecimal getConversionCurrencyRatio(MKTCurrency expenseCurrency, MKTCurrency expectedCurrency);

    BigDecimal sumAllExpensesByCurrency(MKTCurrency expectedCurrency, Budget budget);

    EntityModel<ExpenseResponseDto> getEntityModelFromLink(Link link, Expense expense);

    PagedModel<ExpenseResponseDto> getPagedModel(Link generalLink, Class<?> controller, Page<Expense> expenses);
}
