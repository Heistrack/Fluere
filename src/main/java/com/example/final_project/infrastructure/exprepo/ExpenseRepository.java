package com.example.final_project.infrastructure.exprepo;

import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetIdWrapper;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseIdWrapper;
import com.example.final_project.domain.users.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface ExpenseRepository extends MongoRepository<Expense, ExpenseIdWrapper> {

    List<Expense> findExpenseByBudgetId(BudgetIdWrapper budgetId);

    List<Expense> findAllByBudgetId(BudgetIdWrapper budgetId);

    Optional<Expense> findExpenseByExpenseIdAndUserId(ExpenseIdWrapper expenseId, UserIdWrapper userId);

    Page<Expense> findExpensesByUserId(UserIdWrapper userId, Pageable pageable);

    void deleteExpenseByExpenseIdAndUserId(ExpenseIdWrapper expenseId, UserIdWrapper userId);

    List<Expense> findAllByUserId(UserIdWrapper userId);

    List<Expense> findExpensesByBudgetIdAndUserId(BudgetIdWrapper budgetId, UserIdWrapper userId);

    Page<Expense> findAllByBudgetIdAndUserId(BudgetIdWrapper budgetId, UserIdWrapper userId, Pageable pageable);

    Budget findBudgetByExpenseId(ExpenseIdWrapper expenseId);
}
