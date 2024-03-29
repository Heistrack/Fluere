package com.example.final_project.domain.budgets;

import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.users.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BudgetService {

    Budget getBudgetById(BudgetIdWrapper budgetId, UserIdWrapper userId);

    void deleteBudgetByBudgetId(BudgetIdWrapper budgetId);

    Budget registerNewBudget(String title, BigDecimal limit, BudgetType budgetType, BigDecimal maxSingleExpense,
                             UserIdWrapper userId
    );

    Budget patchBudgetContent(BudgetIdWrapper budgetId, Optional<String> title, Optional<BigDecimal> limit,
                              Optional<BudgetType> typeOfBudget, Optional<BigDecimal> maxSingleExpense, UserIdWrapper userId
    );

    Budget updateBudgetById(BudgetIdWrapper BudgetId, String title, BigDecimal limit, BudgetType budgetType,
                            BigDecimal maxSingleExpense, UserIdWrapper userId);

    Page<Budget> findAllByPage(UserIdWrapper userId, Pageable pageable);

    BudgetStatusDTO getBudgetStatus(BudgetIdWrapper budgetId, UserIdWrapper userId);

    List<Budget> getAllBudgetsByUserId(UserIdWrapper userId);
}
