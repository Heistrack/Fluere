package com.example.fluere.budget.service.user;

import com.example.fluere.budget.controller.user.BudgetController;
import com.example.fluere.budget.model.*;
import com.example.fluere.budget.repository.BudgetRepository;
import com.example.fluere.budget.response.BudgetStatusDTO;
import com.example.fluere.budget.response.BudgetUserMoneySavedDTO;
import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.expense.model.Expense;
import com.example.fluere.expense.model.ExpenseType;
import com.example.fluere.expense.repository.ExpenseRepository;
import com.example.fluere.security.service.jwt.JwtService;
import com.example.fluere.userentity.model.AppUser;
import com.example.fluere.userentity.model.UserIdWrapper;
import com.example.fluere.userentity.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class DefaultBudgetService implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final Supplier<BudgetIdWrapper> budgetIdSupplier;
    private final BudgetInnerServiceLogic innerServiceLogic;
    private final AdminUserService adminUserService;
    private final JwtService jwtService;

    @Override
    public Budget registerNewBudget(String title, BigDecimal limit, BudgetType budgetType,
                                    BigDecimal maxSingleExpense,
                                    MKTCurrency defaultCurrency,
                                    LocalDate budgetStart,
                                    LocalDate budgetEnd,
                                    String description,
                                    Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        String checkedTitle = innerServiceLogic.duplicateBudgetTitleCheck(title, userId);

        BudgetPeriod budgetPeriod = innerServiceLogic.getBudgetPeriod(budgetStart, budgetEnd);

        TreeMap<Integer, LocalDateTime> historyOfChange = new TreeMap<>();
        historyOfChange.put(1, LocalDateTime.now());

        if (maxSingleExpense.compareTo(limit) > 0) {
            maxSingleExpense = limit;
        }

        Budget budget = Budget.newOf(
                budgetIdSupplier.get(), userId,
                BudgetDetails.newOf(checkedTitle, limit,
                                    budgetType, maxSingleExpense,
                                    defaultCurrency,
                                    ExpenseSet.newOf(defaultCurrency),
                                    historyOfChange,
                                    budgetPeriod,
                                    description == null ? "" : description
                )
        );
        return budgetRepository.save(budget);
    }

    @Override
    public Budget getBudgetById(BudgetIdWrapper budgetId, Authentication authentication) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        return budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                               .orElseThrow(() -> new NoSuchElementException("There's no such budget."));
    }

    @Override
    public BudgetStatusDTO getBudgetStatus(BudgetIdWrapper budgetId, Authentication authentication) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Budget budget = budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                                        .orElseThrow(() -> new NoSuchElementException("Budget doesn't exist"));

        BigDecimal totalMoneySpent = innerServiceLogic.showBalanceByCurrency(
                budget.budgetDetails().defaultCurrency(), budget);
        BigDecimal amountLeft = budget.budgetDetails().limit().subtract(totalMoneySpent);
        Float budgetFullFillPercent = innerServiceLogic.budgetFullFillPercentage(
                budget.budgetDetails().limit(), totalMoneySpent);
        List<Expense> allBudgetExpenses = expenseRepository.findAllByBudgetId(budgetId);
        String trueBudgetLimitValue = innerServiceLogic.getTrueLimitFromBudget(budget);
        TreeMap<LocalDate, List<Expense>> expensesByDay = innerServiceLogic.getExpensesByDay(allBudgetExpenses);
        HashMap<ExpenseType, List<Expense>> expensesByCategory = innerServiceLogic.getExpensesByCategory(
                allBudgetExpenses);
        HashMap<ExpenseType, Float> expenseCategoryPercentage = innerServiceLogic.getExpenseCategoryPercentage(
                allBudgetExpenses, budget);

        return BudgetStatusDTO.newOf(
                budgetId.id(),
                budget.budgetDetails(),
                amountLeft,
                totalMoneySpent,
                allBudgetExpenses.size(),
                budgetFullFillPercent,
                trueBudgetLimitValue,
                expensesByDay,
                expensesByCategory,
                expenseCategoryPercentage
        );
    }

    @Override
    public Page<BudgetStatusDTO> getBudgetsStatuses(Pageable pageable, Authentication authentication) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        List<Budget> budgets = budgetRepository.findAllByUserId(userId);
        if (budgets.isEmpty()) throw new NoSuchElementException("Budgets don't exist");

        List<BudgetStatusDTO> budgetsStatus = budgets.stream()
                                                     .map(budget -> getBudgetStatus(budget.budgetId(), authentication))
                                                     .toList();
        return new PageImpl<>(budgetsStatus, pageable, budgetsStatus.size());
    }

    @Override
    public BudgetUserMoneySavedDTO getAllMoneySavedByUser(Authentication authentication) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        return innerServiceLogic.getMoneySavedBySingleUser(userId);
    }

    @Override
    public BudgetUserMoneySavedDTO getAllMoneySaved() {
        List<AppUser> allUsersToList = adminUserService.getAllUsersToList();
        BigDecimal totalMoneySaved = allUsersToList.stream().map(AppUser::getUserId)
                                                   .map(innerServiceLogic::getMoneySavedBySingleUser)
                                                   .map(BudgetUserMoneySavedDTO::getMoneySaved)
                                                   .reduce(BigDecimal.ZERO, BigDecimal::add);
        return BudgetUserMoneySavedDTO.newOf("Total_money_saved", totalMoneySaved);
    }

    @Override
    public Page<Budget> getAllByPage(Pageable pageable, Authentication authentication) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Page<Budget> allByUserId = budgetRepository.findAllByUserId(userId, pageable);

        if (allByUserId.isEmpty()) throw new NoSuchElementException("No results match");
        return allByUserId;
    }

    @Override
    public List<Budget> getAllBudgetsByUserId(Authentication authentication) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        return budgetRepository.findAllByUserId(userId);
    }

    @Override
    public Budget updateBudgetById(BudgetIdWrapper budgetId,
                                   String title,
                                   BigDecimal limit,
                                   BudgetType budgetType,
                                   BigDecimal maxSingleExpense,
                                   MKTCurrency defaultCurrency,
                                   LocalDate budgetStart,
                                   LocalDate budgetEnd,
                                   String description,
                                   Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Budget oldBudget = budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                                           .orElseThrow(() -> new NoSuchElementException(
                                                   "Can't update budget, because it doesn't exist"));

        BudgetPeriod budgetPeriod = innerServiceLogic.getBudgetPeriod(budgetStart, budgetEnd);
        if (innerServiceLogic.noParamChangeCheck(oldBudget, Optional.of(title), Optional.of(limit),
                                                 Optional.of(budgetType), Optional.of(maxSingleExpense),
                                                 Optional.of(defaultCurrency),
                                                 budgetPeriod,
                                                 Optional.ofNullable(description)
        )) {
            return oldBudget;
        }
        if (!title.equals(oldBudget.budgetDetails().title())) {
            title = innerServiceLogic.duplicateBudgetTitleCheck(title, userId);
        }
        if (maxSingleExpense.compareTo(limit) > 0) {
            maxSingleExpense = limit;
        }

        innerServiceLogic.updateHistoryChange(oldBudget);

        return budgetRepository.save(Budget.newOf(
                budgetId,
                userId,
                BudgetDetails.newOf(
                        title,
                        limit,
                        budgetType,
                        maxSingleExpense,
                        defaultCurrency,
                        oldBudget.budgetDetails().expenseSet(),
                        oldBudget.budgetDetails().historyOfChanges(),
                        budgetPeriod,
                        description == null ? "" : description
                )
        ));
    }

    @Override
    public Budget patchBudgetContent(BudgetIdWrapper budgetId,
                                     Optional<String> title,
                                     Optional<BigDecimal> limit,
                                     Optional<BudgetType> budgetType,
                                     Optional<BigDecimal> maxSingleExpense,
                                     Optional<MKTCurrency> defaultCurrency,
                                     Optional<LocalDate> budgetStart,
                                     Optional<LocalDate> budgetEnd,
                                     Optional<String> description,
                                     Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Budget oldBudget = budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                                           .orElseThrow(() -> new NoSuchElementException(
                                                   "Can't update budget, because it doesn't exist"));
        BudgetPeriod budgetPeriod = innerServiceLogic.getBudgetPeriod(
                budgetStart.orElse(oldBudget.budgetDetails().budgetPeriod()
                                            .getStartTime()),
                budgetEnd.orElse(oldBudget.budgetDetails().budgetPeriod()
                                          .getEndTime())
        );
        if (innerServiceLogic.noParamChangeCheck(oldBudget, title, limit, budgetType,
                                                 maxSingleExpense, defaultCurrency, budgetPeriod, description
        )) {
            return oldBudget;
        }
        if (title.isPresent() && !title.get().equals(oldBudget.budgetDetails().title())) {
            title = Optional.of(innerServiceLogic.duplicateBudgetTitleCheck(title.get(), userId));
        }
        if (maxSingleExpense.isPresent() && maxSingleExpense.get().compareTo(
                limit.orElse(oldBudget.budgetDetails().limit())) > 0) {
            maxSingleExpense = limit;
        }

        Optional<BigDecimal> checkedMaxSingleExpense = maxSingleExpense;
        Optional<String> checkedTitle = title;

        innerServiceLogic.updateHistoryChange(oldBudget);

        return budgetRepository.save(budgetRepository.findByBudgetIdAndUserId(budgetId, userId).map(
                budgetFromRepository -> Budget.newOf(
                        budgetId,
                        userId,
                        BudgetDetails.newOf(
                                checkedTitle.orElseGet(() -> budgetFromRepository.budgetDetails().title()),
                                limit.orElseGet(() -> budgetFromRepository.budgetDetails().limit()),
                                budgetType.orElseGet(() -> budgetFromRepository.budgetDetails().budgetType()),
                                checkedMaxSingleExpense.orElseGet(
                                        () -> budgetFromRepository.budgetDetails().maxSingleExpense()),
                                defaultCurrency.orElseGet(() -> budgetFromRepository.budgetDetails().defaultCurrency()),
                                oldBudget.budgetDetails().expenseSet(),
                                oldBudget.budgetDetails().historyOfChanges(),
                                budgetPeriod,
                                description.orElseGet(() -> budgetFromRepository.budgetDetails().description())
                        )
                )).orElseThrow(IllegalArgumentException::new));
    }

    @Override
    public void deleteAllBudgetExpensesByBudgetId(BudgetIdWrapper budgetId, Authentication authentication) {
        BudgetIdWrapper checkedBudgetId = getBudgetById(budgetId, authentication).budgetId();
        expenseRepository.deleteAllByBudgetId(checkedBudgetId);
        budgetRepository.deleteById(budgetId);
    }

    @Override
    public <T extends LinkableDTO> EntityModel<T> getEntityModel(T linkableDTO, Class<T> classCast) {
        Link link = linkTo(BudgetController.class).slash(linkableDTO.PathMessage()).withSelfRel();
        linkableDTO.addLink(link);
        return EntityModel.of(classCast.cast(linkableDTO));
    }

    @Override
    public <T extends LinkableDTO> PagedModel<T> getEntities(Page<T> linkableDTOs, Class<T> classCast) {
        return innerServiceLogic.getPagedModel(linkableDTOs, classCast, BudgetController.class);
    }
}
