package com.example.fluere.expense.repository;

import com.example.fluere.budget.model.BudgetIdWrapper;
import com.example.fluere.expense.model.Expense;
import com.example.fluere.expense.model.ExpenseIdWrapper;
import com.example.fluere.userentity.model.UserIdWrapper;
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

    Page<Expense> findAllByBudgetIdAndUserId(BudgetIdWrapper budgetId, UserIdWrapper userId, Pageable pageable);

    Page<Expense> findAllByBudgetId(BudgetIdWrapper budgetId, Pageable pageable);

    void deleteAllByBudgetId(BudgetIdWrapper budgetId);

    boolean existsByBudgetIdAndExpenseDetails_Title(BudgetIdWrapper budgetId, String title);
}
