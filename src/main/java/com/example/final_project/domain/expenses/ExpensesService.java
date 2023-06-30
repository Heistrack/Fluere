package com.example.final_project.domain.expenses;

import com.example.final_project.domain.budgets.BudgetId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExpensesService {
    Expense registerNewExpense(String title, BigDecimal amount, BudgetId budgetId, String userId, Optional<TypeOfExpense> typeOfExpense);

    Optional<Expense> getExpenseById(ExpenseId expenseId, String userId);

    void deleteExpenseById(ExpenseId expenseId, String userId);

    Optional<Expense> updateExpenseContent(ExpenseId expenseId,
                                           Optional<String> title,
                                           Optional<BigDecimal> amount,
                                           String userId,
                                           Optional<TypeOfExpense> typeOfExpense);

    List<Expense> getExpenses(String userId);

    Expense updateExpenseById(ExpenseId expenseId, BudgetId budgetId, String title, BigDecimal amount, String userId,
                              TypeOfExpense typeOfExpense);

    Page<Expense> findAllExpensesByBudgetId(String userId, BudgetId budgetId, Pageable pageable);

    Page<Expense> findAllByPage(Pageable pageable, String userId);
}
