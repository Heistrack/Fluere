package com.example.fluere.expense.service.user;

import com.example.fluere.budget.model.Budget;
import com.example.fluere.budget.model.BudgetIdWrapper;
import com.example.fluere.budget.model.LinkableDTO;
import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.expense.model.Expense;
import com.example.fluere.expense.model.ExpenseType;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExpenseInnerServiceLogic {
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

    <T extends LinkableDTO> PagedModel<T> getPagedModel(Page<T> linkableDTOs, Class<T> classCast,
                                                        Class<?> controllerClass
    );
}
