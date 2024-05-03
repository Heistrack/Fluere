package com.example.final_project.expense.service.admin;

import com.example.final_project.budget.service.BudgetIdWrapper;
import com.example.final_project.expense.service.Expense;
import com.example.final_project.expense.service.ExpenseIdWrapper;
import com.example.final_project.expense.service.ExpenseType;
import com.example.final_project.userentity.service.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface AdminExpenseService {
    Expense registerNewExpense(BudgetIdWrapper budgetId, UserIdWrapper userId, String title,
                               BigDecimal amount, ExpenseType expenseType, String description
    );

    Expense getExpenseById(ExpenseIdWrapper expenseId);

    Page<Expense> getAllExpensesByBudgetId(BudgetIdWrapper budgetId, Pageable pageable);

    Page<Expense> getAllExpensesByUserId(UserIdWrapper userId, Pageable pageable);

    Page<Expense> getAllExpensesByPage(Pageable pageable);

    Expense updateExpenseById(ExpenseIdWrapper expenseId,
                              String title,
                              BigDecimal amount,
                              Optional<ExpenseType> typeOfExpense,
                              Optional<String> description
    );

    Expense patchExpenseContent(ExpenseIdWrapper expenseId,
                                Optional<String> title,
                                Optional<BigDecimal> amount,
                                Optional<ExpenseType> typeOfExpense,
                                Optional<String> description
    );

    void deleteExpenseById(ExpenseIdWrapper expenseId);
}
