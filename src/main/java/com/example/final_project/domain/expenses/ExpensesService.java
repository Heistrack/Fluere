package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.BudgetIdWrapper;
import com.example.final_project.domain.users.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExpensesService {
    Expense registerNewExpense(String title, BigDecimal amount, BudgetIdWrapper budgetId, UserIdWrapper userId, Optional<TypeOfExpense> typeOfExpense);

    Optional<Expense> getExpenseById(ExpenseIdWrapper expenseId, UserIdWrapper userId);

    void deleteExpenseById(ExpenseIdWrapper expenseId, UserIdWrapper userId);

    Optional<Expense> updateExpenseContent(ExpenseIdWrapper expenseId,
                                           Optional<String> title,
                                           Optional<BigDecimal> amount,
                                           UserIdWrapper userId,
                                           Optional<TypeOfExpense> typeOfExpense);

    List<Expense> getExpenses(UserIdWrapper userId);

    Expense updateExpenseById(ExpenseIdWrapper expenseId, BudgetIdWrapper budgetId, String title, BigDecimal amount, UserIdWrapper userId,
                              TypeOfExpense typeOfExpense);

    Page<Expense> findAllExpensesByBudgetId(UserIdWrapper userId, BudgetIdWrapper budgetId, Pageable pageable);

    Page<Expense> findAllByPage(Pageable pageable, UserIdWrapper userId);
}
