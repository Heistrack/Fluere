package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExpensesService {
    Expense getExpenseById(ExpenseIdWrapper expenseId, UserIdWrapper userId);

    Expense registerNewExpense(String title, BigDecimal amount, BudgetIdWrapper budgetId, UserIdWrapper userId,
                               ExpenseType expenseType
    );

    void deleteExpenseById(ExpenseIdWrapper expenseId, UserIdWrapper userId);

    Expense patchExpenseContent(ExpenseIdWrapper expenseId,
                                Optional<String> title,
                                Optional<BigDecimal> amount,
                                UserIdWrapper userId,
                                Optional<ExpenseType> typeOfExpense
    );

    Expense updateExpenseById(ExpenseIdWrapper expenseId, String title, BigDecimal amount,
                              UserIdWrapper userId,
                              Optional<ExpenseType> typeOfExpense
    );

    Page<Expense> getAllExpensesByBudgetId(UserIdWrapper userId, BudgetIdWrapper budgetId, Pageable pageable);

    Page<Expense> getAllByPage(UserIdWrapper userId, Pageable pageable);
}
