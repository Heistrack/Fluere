package com.example.final_project.infrastructure.exprepo;

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

    List<Expense> findAllByBudgetId(BudgetIdWrapper budgetId);

    Optional<Expense> findByExpenseIdAndUserId(ExpenseIdWrapper expenseId, UserIdWrapper userId);

    Page<Expense> findAllByUserId(UserIdWrapper userId, Pageable pageable);

    void deleteByExpenseIdAndUserId(ExpenseIdWrapper expenseId, UserIdWrapper userId);

    List<Expense> findAllByBudgetIdAndUserId(BudgetIdWrapper budgetId, UserIdWrapper userId);

    Page<Expense> findAllByBudgetIdAndUserId(BudgetIdWrapper budgetId, UserIdWrapper userId, Pageable pageable);

    void deleteAllByBudgetId(BudgetIdWrapper budgetId);

    boolean existsByBudgetIdAndExpenseDetails_Title(BudgetIdWrapper budgetId, String title);
}
