package com.example.final_project.budget.service.user;

import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.budget.model.BudgetType;
import com.example.final_project.budget.response.BudgetResponseDto;
import com.example.final_project.budget.response.BudgetStatusDTO;
import com.example.final_project.currencyapi.model.MKTCurrency;
import com.nimbusds.jose.util.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetService {

    Budget registerNewBudget(String title,
                             BigDecimal limit,
                             BudgetType budgetType,
                             BigDecimal maxSingleExpense,
                             MKTCurrency defaultCurrency,
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

    Pair<UUID, BigDecimal> getAllMoneySaved(Authentication authentication);

    Budget updateBudgetById(BudgetIdWrapper budgetId,
                            String title,
                            BigDecimal limit,
                            BudgetType budgetType,
                            BigDecimal maxSingleExpense,
                            MKTCurrency defaultCurrency,
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
                              Optional<MKTCurrency> defaultCurrency,
                              Optional<LocalDate> budgetStart,
                              Optional<LocalDate> budgetEnd,
                              Optional<String> description,
                              Authentication authentication
    );

    void deleteAllBudgetExpensesByBudgetId(BudgetIdWrapper budgetId, Authentication authentication);

    EntityModel<BudgetResponseDto> getEntityModel(Budget budget);

    PagedModel<BudgetResponseDto> getEntities(Page<Budget> budgets);
}
