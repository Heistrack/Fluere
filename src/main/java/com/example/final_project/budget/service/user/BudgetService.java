package com.example.final_project.budget.service.user;

import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.budget.model.BudgetType;
import com.example.final_project.budget.model.LinkableDTO;
import com.example.final_project.budget.response.BudgetStatusDTO;
import com.example.final_project.budget.response.BudgetUserMoneySavedDTO;
import com.example.final_project.currencyapi.model.MKTCurrency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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

    BudgetUserMoneySavedDTO getAllMoneySavedByUser(Authentication authentication);

    BudgetUserMoneySavedDTO getAllMoneySaved();

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

    <T extends LinkableDTO> EntityModel<T> getEntityModel(T linkableDTO, Class<T> classCast);

    <T extends LinkableDTO> PagedModel<T> getEntities(Page<T> linkableDTOs, Class<T> classCast);
}
