package com.example.final_project.domain.expenses;

import com.example.final_project.api.responses.ExpenseResponseDto;
import com.example.final_project.domain.budgets.BudgetId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExpensesService {
    Expense registerNewExpense(String title, BigDecimal amount, BudgetId budgetId);
    Optional<Expense> getExpenseById(ExpenseId expenseId);

    void deleteExpenseById(ExpenseId expenseId);
    Optional<Expense> updateExpenseContent(ExpenseId expenseId, Optional<String> title, Optional<BigDecimal> amount);

    List<Expense> getExpenses();

    Expense updateExpenseById(ExpenseId expenseId, String title, BigDecimal amount);

    Page<Expense> findAllByPage(Pageable pageable);
}
