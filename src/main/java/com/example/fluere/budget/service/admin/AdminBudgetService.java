package com.example.fluere.budget.service.admin;

import com.example.fluere.budget.model.Budget;
import com.example.fluere.budget.model.BudgetIdWrapper;
import com.example.fluere.budget.model.BudgetType;
import com.example.fluere.budget.model.LinkableDTO;
import com.example.fluere.budget.response.BudgetStatusDTO;
import com.example.fluere.budget.response.BudgetUserMoneySavedDTO;
import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.userentity.model.UserIdWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

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
                             MKTCurrency defaultCurrency,
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

    BudgetUserMoneySavedDTO getAllMoneySavedByUser(UUID userId);

    Budget updateBudgetById(BudgetIdWrapper budgetId,
                            String title,
                            BigDecimal limit,
                            BudgetType budgetType,
                            BigDecimal maxSingleExpense,
                            MKTCurrency defaultCurrency,
                            LocalDate budgetStart,
                            LocalDate budgetEnd,
                            String description
    );

    Budget patchBudgetContent(BudgetIdWrapper budgetId,
                              Optional<String> title,
                              Optional<BigDecimal> limit,
                              Optional<BudgetType> budgetType,
                              Optional<BigDecimal> maxSingleExpense,
                              Optional<MKTCurrency> defaultCurrency,
                              Optional<LocalDate> budgetStart,
                              Optional<LocalDate> budgetEnd,
                              Optional<String> description
    );

    void deleteBudgetByBudgetId(BudgetIdWrapper budgetId);

    <T extends LinkableDTO> EntityModel<T> getEntityModel(T linkableDTO, Class<T> classCast);

    <T extends LinkableDTO> PagedModel<T> getEntities(Page<T> linkableDTOs, Class<T> classCast);
}
