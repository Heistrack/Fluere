package com.example.final_project.infrastructure.bdtrepo;

import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetIdWrapper;
import com.example.final_project.domain.users.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BudgetRepository extends MongoRepository<Budget, BudgetIdWrapper> {

    Optional<Budget> findByBudgetIdAndUserId(BudgetIdWrapper budgetId, UserIdWrapper userId);

    void deleteByBudgetIdAndUserId(BudgetIdWrapper budgetId, UserIdWrapper userId);

    Page<Budget> findAllByUserId(UserIdWrapper userId, Pageable pageable);

    boolean existsByBudgetIdAndUserId(BudgetIdWrapper budgetId, UserIdWrapper userId);
}
