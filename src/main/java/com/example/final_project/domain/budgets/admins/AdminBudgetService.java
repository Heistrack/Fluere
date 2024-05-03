package com.example.final_project.domain.budgets.admins;

import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.budgets.appusers.Budget;
import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import com.example.final_project.domain.budgets.appusers.BudgetType;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminBudgetService {

    Budget registerNewBudget(UserIdWrapper userIdWrapper,
                             String title,
                             BigDecimal limit,
                             BudgetType budgetType,
                             BigDecimal maxSingleExpense,
                             LocalDate budgetStart,
                             LocalDate budgetEnd,
                             String description

    );

    Budget getBudgetById(BudgetIdWrapper budgetId);

    Page<Budget> getAllBudgetsByPage(Pageable pageable);

    Page<Budget> getAllBudgetsByUserIdAndPage(UUID userId, Pageable pageable);

    List<Budget> getAllBudgetsByUserId(UserIdWrapper userId);

    BudgetStatusDTO getBudgetStatus(BudgetIdWrapper budgetId);

    Page<BudgetStatusDTO> getBudgetsStatuses(Pageable pageable, UserIdWrapper userId);

    Budget updateBudgetById(BudgetIdWrapper budgetId,
                            String title,
                            BigDecimal limit,
                            BudgetType budgetType,
                            BigDecimal maxSingleExpense,
                            LocalDate budgetStart,
                            LocalDate budgetEnd,
                            String description
    );

    Budget patchBudgetContent(BudgetIdWrapper budgetId,
                              Optional<String> title,
                              Optional<BigDecimal> limit,
                              Optional<BudgetType> budgetType,
                              Optional<BigDecimal> maxSingleExpense,
                              Optional<LocalDate> budgetStart,
                              Optional<LocalDate> budgetEnd,
                              Optional<String> description
    );

    void deleteBudgetByBudgetId(BudgetIdWrapper budgetId);
}
