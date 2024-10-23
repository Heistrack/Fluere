package com.example.fluere.budget.repository;

import com.example.fluere.budget.model.Budget;
import com.example.fluere.budget.model.BudgetIdWrapper;
import com.example.fluere.userentity.model.UserIdWrapper;
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
