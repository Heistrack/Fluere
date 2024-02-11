package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.users.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExpensesService {
    Expense registerNewExpense(String title, BigDecimal amount, BudgetId budgetId, UserId userId, Optional<TypeOfExpense> typeOfExpense);

    Optional<Expense> getExpenseById(ExpenseId expenseId, UserId userId);

    void deleteExpenseById(ExpenseId expenseId, UserId userId);

    Optional<Expense> updateExpenseContent(ExpenseId expenseId,
                                           Optional<String> title,
                                           Optional<BigDecimal> amount,
                                           UserId userId,
                                           Optional<TypeOfExpense> typeOfExpense);

    List<Expense> getExpenses(UserId userId);

    Expense updateExpenseById(ExpenseId expenseId, BudgetId budgetId, String title, BigDecimal amount, UserId userId,
                              TypeOfExpense typeOfExpense);

    Page<Expense> findAllExpensesByBudgetId(UserId userId, BudgetId budgetId, Pageable pageable);

    Page<Expense> findAllByPage(Pageable pageable, UserId userId);
}
