package com.example.final_project.infrastructure.bdtrepo;

import com.example.final_project.domain.budgets.appusers.Budget;
import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends MongoRepository<Budget, BudgetIdWrapper> {

    Optional<Budget> findByBudgetIdAndUserId(BudgetIdWrapper budgetId, UserIdWrapper userId);

    Page<Budget> findAllByUserId(UserIdWrapper userId, Pageable pageable);

    boolean existsByUserIdAndBudgetDetails_Title(UserIdWrapper userId, String title);

    List<Budget> findAllByUserId(UserIdWrapper userId);
}
