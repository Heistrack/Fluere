package com.example.final_project.infrastructure.exprepo;

import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface ExpenseRepository extends MongoRepository<Expense, ExpenseId> {


    Optional<Expense> findExpenseByExpenseIdAndUserId(ExpenseId expenseId, String userId);

    List<Expense> findExpenseByBudgetIdAndUserId(BudgetId budgetId, String userId);

    Page<Expense> findExpensesByUserId(String userId, Pageable pageable);

    void deleteExpenseByExpenseIdAndUserId(ExpenseId expenseId, String userId);

    List<Expense> findAllByUserId(String userId);

    List<Expense> findExpensesByBudgetIdAndUserId(BudgetId budgetId, String userId);

    Page<Expense> findAllByBudgetIdAndUserId(BudgetId budgetId, String userId, Pageable pageable);

    Budget findBudgetByExpenseId(ExpenseId expenseId);
}
