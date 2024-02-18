package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.BudgetIdWrapper;
import com.example.final_project.domain.users.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExpensesService {
    Expense getExpenseById(ExpenseIdWrapper expenseId, UserIdWrapper userId);

    Expense registerNewExpense(String title, BigDecimal amount, BudgetIdWrapper budgetId, UserIdWrapper userId,
                               TypeOfExpense typeOfExpense
    );

    void deleteExpenseById(ExpenseIdWrapper expenseId, UserIdWrapper userId);

    Expense patchExpenseContent(ExpenseIdWrapper expenseId,
                                Optional<String> title,
                                Optional<BigDecimal> amount,
                                UserIdWrapper userId,
                                Optional<TypeOfExpense> typeOfExpense
    );

    Expense updateExpenseById(ExpenseIdWrapper expenseId, String title, BigDecimal amount,
                              UserIdWrapper userId,
                              Optional<TypeOfExpense> typeOfExpense
    );

    Page<Expense> findAllExpensesByBudgetId(UserIdWrapper userId, BudgetIdWrapper budgetId, Pageable pageable);

    Page<Expense> findAllByPage(UserIdWrapper userId, Pageable pageable);
}
