package com.example.final_project.expense.service.user;

import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.budget.model.LinkableDTO;
import com.example.final_project.currencyapi.model.MKTCurrency;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseIdWrapper;
import com.example.final_project.expense.model.ExpenseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExpenseService {
    Expense registerNewExpense(
            BudgetIdWrapper budgetId,
            String title,
            BigDecimal amount,
            MKTCurrency currency,
            ExpenseType expenseType,
            String description,
            Authentication authentication
    );

    Expense getExpenseById(ExpenseIdWrapper expenseId, Authentication authentication);

    Page<Expense> getAllExpensesByBudgetId(BudgetIdWrapper budgetId, Pageable pageable, Authentication authentication);

    Page<Expense> getAllByPage(Pageable pageable, Authentication authentication);

    Expense updateExpenseById(ExpenseIdWrapper expenseId,
                              String title,
                              BigDecimal amount,
                              MKTCurrency currency,
                              ExpenseType typeOfExpense,
                              String description,
                              Authentication authentication
    );

    Expense patchExpenseContent(ExpenseIdWrapper expenseId,
                                Optional<String> title,
                                Optional<BigDecimal> amount,
                                Optional<MKTCurrency> currency,
                                Optional<ExpenseType> typeOfExpense,
                                Optional<String> description,
                                Authentication authentication
    );

    void deleteExpenseById(ExpenseIdWrapper expenseId, Authentication authentication);

    <T extends LinkableDTO> EntityModel<T> getEntityModel(T linkableDTO, Class<T> classCast);

    <T extends LinkableDTO> PagedModel<T> getEntities(Page<T> linkableDTOs, Class<T> classCast);
}
