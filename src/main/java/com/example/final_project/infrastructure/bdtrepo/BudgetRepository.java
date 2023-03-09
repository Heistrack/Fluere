package com.example.final_project.infrastructure.bdtrepo;

import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BudgetRepository extends MongoRepository<Budget, BudgetId> {
    @Cacheable("findBudgetCache")
    Optional<Budget> findBudgetByBudgetId(BudgetId budgetId);
}
