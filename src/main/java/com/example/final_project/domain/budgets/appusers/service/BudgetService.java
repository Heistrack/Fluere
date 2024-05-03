package com.example.final_project.domain.budgets.appusers.service;

import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.budgets.appusers.Budget;
import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import com.example.final_project.domain.budgets.appusers.BudgetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetService {

    Budget registerNewBudget(String title,
                             BigDecimal limit,
                             BudgetType budgetType,
                             BigDecimal maxSingleExpense,
                             LocalDate budgetStart,
                             LocalDate budgetEnd,
                             String description,
                             Authentication authentication
    );

    Budget getBudgetById(BudgetIdWrapper budgetId, Authentication authentication);

    Page<Budget> getAllByPage(Pageable pageable, Authentication authentication);

    List<Budget> getAllBudgetsByUserId(Authentication authentication);

    BudgetStatusDTO getBudgetStatus(BudgetIdWrapper budgetId, Authentication authentication);

    Page<BudgetStatusDTO> getBudgetsStatuses(Pageable pageable, Authentication authentication);

    Budget updateBudgetById(BudgetIdWrapper budgetId,
                            String title,
                            BigDecimal limit,
                            BudgetType budgetType,
                            BigDecimal maxSingleExpense,
                            LocalDate budgetStart,
                            LocalDate budgetEnd,
                            String description,
                            Authentication authentication
    );

    Budget patchBudgetContent(BudgetIdWrapper budgetId,
                              Optional<String> title,
                              Optional<BigDecimal> limit,
                              Optional<BudgetType> budgetType,
                              Optional<BigDecimal> maxSingleExpense,
                              Optional<LocalDate> budgetStart,
                              Optional<LocalDate> budgetEnd,
                              Optional<String> description,
                              Authentication authentication
    );

    void deleteAllBudgetExpensesByBudgetId(BudgetIdWrapper budgetId, Authentication authentication);
}
