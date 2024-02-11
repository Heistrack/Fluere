package com.example.final_project.infrastructure.bdtrepo;

import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.users.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends MongoRepository<Budget, BudgetId> {

    Optional<Budget> findBudgetByBudgetIdAndUserId(BudgetId budgetId, UserId userId);

    void deleteBudgetByBudgetIdAndUserId(BudgetId budgetId, UserId userId);

    Page<Budget> findAllByUserId(UserId userId, Pageable pageable);

    List<Budget> findAllByUserId(UserId userId);
}
