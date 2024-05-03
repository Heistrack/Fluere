package com.example.final_project.expense.service.user;

import com.example.final_project.budget.service.BudgetIdWrapper;
import com.example.final_project.expense.service.Expense;
import com.example.final_project.expense.service.ExpenseIdWrapper;
import com.example.final_project.expense.service.ExpenseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExpensesService {
    Expense registerNewExpense(String title,
                               BigDecimal amount,
                               BudgetIdWrapper budgetId,
                               Authentication authentication,
                               ExpenseType expenseType,
                               String description
    );

    Expense getExpenseById(ExpenseIdWrapper expenseId, Authentication authentication);

    Page<Expense> getAllExpensesByBudgetId(Authentication authentication, BudgetIdWrapper budgetId, Pageable pageable);

    Page<Expense> getAllByPage(Authentication authentication, Pageable pageable);

    Expense updateExpenseById(ExpenseIdWrapper expenseId,
                              String title,
                              BigDecimal amount,
                              Authentication authentication,
                              Optional<ExpenseType> typeOfExpense,
                              Optional<String> description
    );

    Expense patchExpenseContent(ExpenseIdWrapper expenseId,
                                Optional<String> title,
                                Optional<BigDecimal> amount,
                                Authentication authentication,
                                Optional<ExpenseType> typeOfExpense,
                                Optional<String> description
    );

    void deleteExpenseById(ExpenseIdWrapper expenseId, Authentication authentication);
}
