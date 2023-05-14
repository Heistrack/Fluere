package com.example.final_project.domain.budgets;

import com.example.final_project.api.responses.BudgetStatusDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BudgetService {

    Budget registerNewBudget(String title, BigDecimal limit, TypeOfBudget typeOfBudget, BigDecimal maxSingleExpense, String userId);

    Optional<Budget> getBudgetById(BudgetId budgetId, String userId);

    void deleteBudgetById(BudgetId budgetId, String userId);

    Optional<Budget> updateBudgetContent(BudgetId budgetId, Optional<String> title, Optional<BigDecimal> limit,
                                         Optional<TypeOfBudget> typeOfBudget, Optional<BigDecimal> maxSingleExpense, String userId,
                                         Optional<LocalDateTime> timestamp);

    List<Budget> getBudgets(String userId);

    Budget updateBudgetById(BudgetId BudgetId, String title, BigDecimal limit, TypeOfBudget typeOfBudget,
                            BigDecimal maxSingleExpense, String userId, LocalDateTime timestamp);

    Page<Budget> findAllByPage(String userId, Pageable pageable);

    BudgetStatusDTO getBudgetStatus(BudgetId budgetId, String userId);


}
