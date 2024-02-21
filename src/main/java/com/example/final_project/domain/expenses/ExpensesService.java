package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExpensesService {
    Expense registerNewExpense(String title, BigDecimal amount, BudgetIdWrapper budgetId, Authentication authentication,
                               ExpenseType expenseType
    );

    Expense getExpenseById(ExpenseIdWrapper expenseId, Authentication authentication);

    Page<Expense> getAllExpensesByBudgetId(Authentication authentication, BudgetIdWrapper budgetId, Pageable pageable);

    Page<Expense> getAllByPage(Authentication authentication, Pageable pageable);

    Expense updateExpenseById(ExpenseIdWrapper expenseId, String title, BigDecimal amount,
                              Authentication authentication,
                              Optional<ExpenseType> typeOfExpense
    );

    Expense patchExpenseContent(ExpenseIdWrapper expenseId,
                                Optional<String> title,
                                Optional<BigDecimal> amount,
                                Authentication authentication,
                                Optional<ExpenseType> typeOfExpense
    );

    void deleteExpenseById(ExpenseIdWrapper expenseId, Authentication authentication);
}
