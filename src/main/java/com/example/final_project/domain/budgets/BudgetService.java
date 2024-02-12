package com.example.final_project.domain.budgets;

import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.users.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BudgetService {

    Budget registerNewBudget(String title, BigDecimal limit, TypeOfBudget typeOfBudget, BigDecimal maxSingleExpense, UserId userId);

    Optional<Budget> getBudgetById(BudgetId budgetId, UserId userId);

    void deleteBudgetById(BudgetId budgetId, UserId userId);

    Optional<Budget> updateBudgetContent(BudgetId budgetId, Optional<String> title, Optional<BigDecimal> limit,
                                         Optional<TypeOfBudget> typeOfBudget, Optional<BigDecimal> maxSingleExpense, UserId userId,
                                         Optional<LocalDateTime> timestamp);

    List<Budget> getBudgets(UserId userId);

    Budget updateBudgetById(BudgetId BudgetId, String title, BigDecimal limit, TypeOfBudget typeOfBudget,
                            BigDecimal maxSingleExpense, UserId userId, LocalDateTime timestamp);

    Page<Budget> findAllByPage(UserId userId, Pageable pageable);

    BudgetStatusDTO getBudgetStatus(BudgetId budgetId, UserId userId);

}
