package com.example.final_project.infrastructure.bdtrepo;

import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.expenses.ExpenseId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends MongoRepository<Budget, BudgetId> {

    Optional<Budget> findBudgetByBudgetIdAndUserId(BudgetId budgetId, String userId);

    void deleteBudgetByBudgetIdAndUserId(BudgetId budgetId, String userId);

    Page<Budget> findAllByUserId(String userId, Pageable pageable);

    List<Budget> findAllByUserId(String userId);
}
