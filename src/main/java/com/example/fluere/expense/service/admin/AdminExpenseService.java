package com.example.fluere.expense.service.admin;

import com.example.fluere.budget.model.BudgetIdWrapper;
import com.example.fluere.budget.model.LinkableDTO;
import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.expense.model.Expense;
import com.example.fluere.expense.model.ExpenseIdWrapper;
import com.example.fluere.expense.model.ExpenseType;
import com.example.fluere.userentity.model.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.math.BigDecimal;
import java.util.Optional;

public interface AdminExpenseService {
    Expense registerNewExpense(BudgetIdWrapper budgetId, String title,
                               BigDecimal amount, MKTCurrency currency, ExpenseType expenseType, String description
    );

    Expense getExpenseById(ExpenseIdWrapper expenseId);

    Page<Expense> getAllExpensesByBudgetId(BudgetIdWrapper budgetId, Pageable pageable);

    Page<Expense> getAllExpensesByUserId(UserIdWrapper userId, Pageable pageable);

    Page<Expense> getAllExpensesByPage(Pageable pageable);

    Expense updateExpenseById(ExpenseIdWrapper expenseId,
                              String title,
                              BigDecimal amount,
                              MKTCurrency currency,
                              ExpenseType typeOfExpense,
                              String description
    );

    Expense patchExpenseContent(ExpenseIdWrapper expenseId,
                                Optional<String> title,
                                Optional<BigDecimal> amount,
                                Optional<MKTCurrency> currency,
                                Optional<ExpenseType> typeOfExpense,
                                Optional<String> description
    );

    void deleteExpenseById(ExpenseIdWrapper expenseId);

    <T extends LinkableDTO> EntityModel<T> getEntityModel(T linkableDTO, Class<T> classCast);

    <T extends LinkableDTO> PagedModel<T> getEntities(Page<T> linkableDTOs, Class<T> classCast);
}
