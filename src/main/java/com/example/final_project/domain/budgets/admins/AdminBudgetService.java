package com.example.final_project.domain.budgets.admins;

import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.budgets.appusers.Budget;
import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import com.example.final_project.domain.budgets.appusers.BudgetType;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminBudgetService {

    Budget registerNewBudget(String title, BigDecimal limit, BudgetType budgetType, BigDecimal maxSingleExpense,
                             UserIdWrapper userIdWrapper
    );

    Budget getBudgetById(BudgetIdWrapper budgetId);

    BudgetStatusDTO getBudgetStatus(BudgetIdWrapper budgetId);

    Page<Budget> getAllBudgetsByUserIdAndPage(UUID userId, Pageable pageable);

    Page<Budget> getAllBudgetsByPage(Pageable pageable);

    List<Budget> getAllBudgetsByUserId(UserIdWrapper userId);

    Budget patchBudgetContent(BudgetIdWrapper budgetId, Optional<String> title, Optional<BigDecimal> limit,
                              Optional<BudgetType> typeOfBudget, Optional<BigDecimal> maxSingleExpense
    );

    Budget updateBudgetById(BudgetIdWrapper budgetId, String title, BigDecimal limit, BudgetType budgetType,
                            BigDecimal maxSingleExpense
    );

    void deleteBudgetByBudgetId(BudgetIdWrapper budgetId);
}
