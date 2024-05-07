package com.example.final_project.budget.service.admin;

import com.example.final_project.budget.model.*;
import com.example.final_project.budget.repository.BudgetRepository;
import com.example.final_project.budget.response.BudgetStatusDTO;
import com.example.final_project.budget.service.user.BudgetServiceLogic;
import com.example.final_project.currencyapi.model.MKTCurrency;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseType;
import com.example.final_project.expense.repository.ExpenseRepository;
import com.example.final_project.userentity.model.UserIdWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AdminDefaultBudgetService implements AdminBudgetService {
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetServiceLogic budgetServiceLogic;
    private final Supplier<BudgetIdWrapper> budgetIdSupplier;

    @Override
    public Budget registerNewBudget(UserIdWrapper userId, String title, BigDecimal limit,
                                    BudgetType budgetType, BigDecimal maxSingleExpense,
                                    MKTCurrency defaultCurrency,
                                    LocalDate budgetStart,
                                    LocalDate budgetEnd,
                                    String description
    ) {
        String checkedTitle = budgetServiceLogic.duplicateBudgetTitleCheck(title, userId);
        BudgetPeriod budgetPeriod = budgetServiceLogic.getBudgetPeriod(budgetStart, budgetEnd);

        TreeMap<Integer, LocalDateTime> historyOfChange = new TreeMap<>();
        historyOfChange.put(1, LocalDateTime.now());

        if (maxSingleExpense.compareTo(limit) > 0) {
            maxSingleExpense = limit;
        }

        Budget budget = Budget.newOf(budgetIdSupplier.get(), userId, BudgetDetails.newOf(checkedTitle, limit,
                                                                                         budgetType, maxSingleExpense,
                                                                                         defaultCurrency,
                                                                                         ExpenseSet.newOf(
                                                                                                 defaultCurrency),
                                                                                         historyOfChange,
                                                                                         budgetPeriod,
                                                                                         description == null ? "" : description
        ));
        return budgetRepository.save(budget);
    }

    @Override
    public Budget getBudgetById(BudgetIdWrapper budgetId) {
        return budgetRepository.findById(budgetId)
                               .orElseThrow(() -> new NoSuchElementException("There's no such budget."));
    }

    @Override
    public BudgetStatusDTO getBudgetStatus(BudgetIdWrapper budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                                        .orElseThrow(() -> new NoSuchElementException("Budget doesn't exist"));

        BigDecimal totalMoneySpent = budgetServiceLogic.showBalanceByCurrency(
                budget.budgetDetails().defaultCurrency(), budget);
        BigDecimal amountLeft = budget.budgetDetails().limit().subtract(totalMoneySpent);
        Float budgetFullFillPercent = budgetServiceLogic.budgetFullFillPercentage(
                budget.budgetDetails().limit(), totalMoneySpent);
        List<Expense> allBudgetExpenses = expenseRepository.findAllByBudgetId(budgetId);
        Integer expensesNumber = allBudgetExpenses.size();
        String trueBudgetLimitValue = budgetServiceLogic.getTrueLimitFromBudget(budget);
        TreeMap<LocalDate, List<Expense>> expensesByDay = budgetServiceLogic.getExpensesByDay(allBudgetExpenses);
        HashMap<ExpenseType, List<Expense>> expensesByCategory = budgetServiceLogic.getExpensesByCategory(
                allBudgetExpenses);
        HashMap<ExpenseType, Float> expenseCategoryPercentage = budgetServiceLogic.getExpenseCategoryPercentage(
                allBudgetExpenses, budget);

        return BudgetStatusDTO.newOf(
                budgetId.id(),
                budget.budgetDetails(),
                amountLeft,
                totalMoneySpent,
                expensesNumber,
                budgetFullFillPercent,
                trueBudgetLimitValue,
                expensesByDay,
                expensesByCategory,
                expenseCategoryPercentage
        );
    }

    @Override
    public Page<BudgetStatusDTO> getBudgetsStatuses(Pageable pageable, UserIdWrapper userId) {
        List<Budget> budgets = budgetRepository.findAllByUserId(userId);
        if (budgets.isEmpty()) throw new NoSuchElementException("Budgets don't exist");

        List<BudgetStatusDTO> budgetsStatus = budgets.stream()
                                                     .map(budget -> getBudgetStatus(budget.budgetId()))
                                                     .toList();
        return new PageImpl<>(budgetsStatus, pageable, budgetsStatus.size());
    }

    @Override
    public Page<Budget> getAllBudgetsByUserIdAndPage(UUID userId, Pageable pageable) {
        Page<Budget> allByUserId = budgetRepository.findAllByUserId(UserIdWrapper.newOf(userId), pageable);
        if (allByUserId.isEmpty()) throw new NoSuchElementException("No results match");
        return allByUserId;
    }

    @Override
    public Page<Budget> getAllBudgetsByPage(Pageable pageable) {
        Page<Budget> allByBudgetsByPage = budgetRepository.findAll(pageable);
        if (allByBudgetsByPage.isEmpty()) throw new NoSuchElementException("No results match");
        return allByBudgetsByPage;
    }

    @Override
    public List<Budget> getAllBudgetsByUserId(UserIdWrapper userId) {
        return budgetRepository.findAllByUserId(userId);
    }

    @Override
    public Budget updateBudgetById(BudgetIdWrapper budgetId, String title, BigDecimal limit, BudgetType budgetType,
                                   BigDecimal maxSingleExpense, MKTCurrency defaultCurrency, LocalDate budgetStart,
                                   LocalDate budgetEnd, String description
    ) {
        Budget oldBudget = budgetRepository.findById(budgetId)
                                           .orElseThrow(() -> new NoSuchElementException(
                                                   "Can't update budget, because it doesn't exist"));
        UserIdWrapper userId = oldBudget.userId();

        BudgetPeriod budgetPeriod = budgetServiceLogic.getBudgetPeriod(budgetStart, budgetEnd);
        if (budgetServiceLogic.noParamChangeCheck(oldBudget, Optional.of(title), Optional.of(limit),
                                                  Optional.of(budgetType),
                                                  Optional.of(maxSingleExpense),
                                                  Optional.of(defaultCurrency),
                                                  budgetPeriod,
                                                  Optional.ofNullable(description)
        )) {
            return oldBudget;
        }
        if (!title.equals(oldBudget.budgetDetails().title())) {
            title = budgetServiceLogic.duplicateBudgetTitleCheck(title, userId);
        }
        if (maxSingleExpense.compareTo(limit) > 0) {
            maxSingleExpense = limit;
        }

        budgetServiceLogic.updateHistoryChange(oldBudget);

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
                                     Optional<String> description
    ) {
        Budget oldBudget = budgetRepository.findById(budgetId)
                                           .orElseThrow(() -> new NoSuchElementException(
                                                   "Can't update budget, because it doesn't exist"));
        BudgetPeriod budgetPeriod = budgetServiceLogic.getBudgetPeriod(
                budgetStart.orElse(oldBudget.budgetDetails().budgetPeriod()
                                            .getStartTime()),
                budgetEnd.orElse(oldBudget.budgetDetails().budgetPeriod()
                                          .getEndTime())
        );
        if (budgetServiceLogic.noParamChangeCheck(
                oldBudget, title, limit, budgetType, maxSingleExpense, defaultCurrency, budgetPeriod, description)) {
            return oldBudget;
        }
        if (title.isPresent() && !title.get().equals(oldBudget.budgetDetails().title())) {
            title = Optional.of(budgetServiceLogic.duplicateBudgetTitleCheck(title.get(), oldBudget.userId()));
        }
        if (maxSingleExpense.isPresent() && maxSingleExpense.get().compareTo(
                limit.orElse(oldBudget.budgetDetails().limit())) > 0) {
            maxSingleExpense = limit;
        }

        Optional<String> checkedTitle = title;
        Optional<BigDecimal> checkedMaxSingleExpense = maxSingleExpense;

        budgetServiceLogic.updateHistoryChange(oldBudget);

        return budgetRepository.save(budgetRepository.findById(budgetId).map(
                budgetFromRepository -> Budget.newOf(
                        budgetId,
                        oldBudget.userId(),
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
    public void deleteBudgetByBudgetId(BudgetIdWrapper budgetId) {
        expenseRepository.deleteAllByBudgetId(budgetId);
        budgetRepository.deleteById(budgetId);
    }
}
