package com.example.final_project.domain.budgets.appusers;

import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BudgetService {

    Budget registerNewBudget(String title, BigDecimal limit, BudgetType budgetType, BigDecimal maxSingleExpense,
                             Authentication authentication
    );

    Budget getBudgetById(BudgetIdWrapper budgetId, Authentication authentication);

    Page<Budget> getAllByPage(Authentication authentication, Pageable pageable);

    BudgetStatusDTO getBudgetStatus(BudgetIdWrapper budgetId, Authentication authentication);

    List<Budget> getAllBudgetsByUserId(Authentication authentication);

    Budget updateBudgetById(BudgetIdWrapper budgetId, String title, BigDecimal limit, BudgetType budgetType,
                            BigDecimal maxSingleExpense, Authentication authentication
    );

    Budget patchBudgetContent(BudgetIdWrapper budgetId, Optional<String> title, Optional<BigDecimal> limit,
                              Optional<BudgetType> typeOfBudget, Optional<BigDecimal> maxSingleExpense,
                              Authentication authentication
    );

    void deleteAllBudgetExpensesByBudgetId(BudgetIdWrapper budgetId, Authentication authentication);
}
