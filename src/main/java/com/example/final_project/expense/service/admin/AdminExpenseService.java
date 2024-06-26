package com.example.final_project.expense.service.admin;

import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.budget.model.LinkableDTO;
import com.example.final_project.currencyapi.model.MKTCurrency;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseIdWrapper;
import com.example.final_project.expense.model.ExpenseType;
import com.example.final_project.expense.response.ExpenseResponseDto;
import com.example.final_project.userentity.model.UserIdWrapper;
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
