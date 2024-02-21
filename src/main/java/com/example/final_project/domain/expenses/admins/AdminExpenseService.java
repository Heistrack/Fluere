package com.example.final_project.domain.expenses.admins;

import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseIdWrapper;
import com.example.final_project.domain.expenses.ExpenseType;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface AdminExpenseService {
    Expense registerNewExpense(String title, BigDecimal amount, BudgetIdWrapper budgetId,
                               ExpenseType expenseType, UserIdWrapper userId
    );

    Expense getExpenseById(ExpenseIdWrapper expenseId);

    Page<Expense> getAllExpensesByBudgetId(BudgetIdWrapper budgetId, Pageable pageable);

    Page<Expense> getAllExpensesByUserId(UserIdWrapper userId, Pageable pageable);

    Page<Expense> getAllExpensesByPage(Pageable pageable);

    Expense updateExpenseById(ExpenseIdWrapper expenseId, String title, BigDecimal amount,
                              Optional<ExpenseType> typeOfExpense
    );

    Expense patchExpenseContent(ExpenseIdWrapper expenseId,
                                Optional<String> title,
                                Optional<BigDecimal> amount,
                                Optional<ExpenseType> typeOfExpense
    );

    void deleteExpenseById(ExpenseIdWrapper expenseId);
}
