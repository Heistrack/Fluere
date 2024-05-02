package com.example.final_project.domain.budgets.admins;

import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.budgets.appusers.*;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseDetails;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
import com.example.final_project.infrastructure.exprepo.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AdminDefaultBudgetService implements AdminBudgetService {
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final Supplier<BudgetIdWrapper> budgetIdSupplier;

    @Override
    public Budget registerNewBudget(UserIdWrapper userId, String title, BigDecimal limit,
                                    BudgetType budgetType, BigDecimal maxSingleExpense,
                                    LocalDate budgetStart,
                                    LocalDate budgetEnd,
                                    String description
    ) {
        String checkedTitle = duplicateBudgetTitleCheck(title, userId);
        BudgetPeriod budgetPeriod = getBudgetPeriod(budgetStart, budgetEnd);

        TreeMap<Integer, LocalDateTime> historyOfChange = new TreeMap<>();
        historyOfChange.put(1, LocalDateTime.now());

        if (maxSingleExpense.compareTo(limit) > 0) {
            maxSingleExpense = limit;
        }

        Budget budget = Budget.newOf(budgetIdSupplier.get(), userId, BudgetDetails.newOf(
                checkedTitle,
                limit,
                budgetType,
                maxSingleExpense,
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
                                        .orElseThrow(() -> new NoSuchElementException("There's no such budget."));
        BigDecimal moneySpend = totalExpensesValueSum(budget);
        BigDecimal amountLeft = budget.budgetDetails().limit().subtract(moneySpend);
        BigDecimal budgetFullFillPercent = budgetFullFillPercentage(budget.budgetDetails().limit(), moneySpend);
        Integer expensesNumber = expenseRepository.findAllByBudgetId(budgetId).size();
        String limitValue = getLimitFromBudget(budget);

        return BudgetStatusDTO.newOf(budgetId.id(), expensesNumber,
                                     moneySpend, amountLeft,
                                     budgetFullFillPercent, budget.budgetDetails().budgetType().getTitle(),
                                     limitValue,
                                     budget.budgetDetails().maxSingleExpense(),
                                     budget.budgetDetails().historyOfChanges()
        );
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
    public Budget patchBudgetContent(BudgetIdWrapper budgetId,
                                     Optional<String> title,
                                     Optional<BigDecimal> limit,
                                     Optional<BudgetType> budgetType,
                                     Optional<BigDecimal> maxSingleExpense,
                                     Optional<LocalDate> budgetStart,
                                     Optional<LocalDate> budgetEnd,
                                     Optional<String> description
    ) {
        Budget oldBudget = budgetRepository.findById(budgetId)
                                           .orElseThrow(() -> new NoSuchElementException(
                                                   "Can't update budget, because it doesn't exist"));
        UserIdWrapper userId = oldBudget.userId();

        BudgetPeriod budgetPeriod = getBudgetPeriod(budgetStart.orElse(oldBudget.budgetDetails().budgetPeriod()
                                                                                .getStartTime()),
                                                    budgetEnd.orElse(oldBudget.budgetDetails().budgetPeriod()
                                                                              .getEndTime()));

        if (noParamChangeCheck(oldBudget, title, limit, budgetType, maxSingleExpense, budgetPeriod, description)) {
            return oldBudget;
        }
        if (title.isPresent() && !title.get().equals(oldBudget.budgetDetails().title())) {
            title = Optional.of(duplicateBudgetTitleCheck(title.get(), userId));
        }
        if (maxSingleExpense.isPresent() && maxSingleExpense.get().compareTo(
                limit.orElse(oldBudget.budgetDetails().limit())) > 0) {
            maxSingleExpense = limit;
        }

        Optional<String> checkedTitle = title;
        Optional<BigDecimal> checkedMaxSingleExpense = maxSingleExpense;

        updateHistoryChange(oldBudget);

        return budgetRepository.save(budgetRepository.findById(budgetId).map(
                budgetFromRepository -> Budget.newOf(
                        budgetId,
                        userId,
                        BudgetDetails.newOf(
                                checkedTitle.orElseGet(() -> budgetFromRepository.budgetDetails().title()),
                                limit.orElseGet(() -> budgetFromRepository.budgetDetails().limit()),
                                budgetType.orElseGet(() -> budgetFromRepository.budgetDetails().budgetType()),
                                checkedMaxSingleExpense.orElseGet(
                                        () -> budgetFromRepository.budgetDetails().maxSingleExpense()),
                                oldBudget.budgetDetails().historyOfChanges(),
                                budgetPeriod,
                                description.orElseGet(() -> budgetFromRepository.budgetDetails().description())

                        )
                )).orElseThrow(IllegalArgumentException::new));
    }

    @Override
    public Budget updateBudgetById(BudgetIdWrapper budgetId, String title, BigDecimal limit, BudgetType budgetType,
                                   BigDecimal maxSingleExpense, LocalDate budgetStart,
                                   LocalDate budgetEnd, String description
    ) {
        Budget oldBudget = budgetRepository.findById(budgetId)
                                           .orElseThrow(() -> new NoSuchElementException(
                                                   "Can't update budget, because it doesn't exist"));
        UserIdWrapper userId = oldBudget.userId();

        BudgetPeriod budgetPeriod = getBudgetPeriod(budgetStart, budgetEnd);
        if (noParamChangeCheck(oldBudget, Optional.of(title), Optional.of(limit), Optional.of(budgetType),
                               Optional.of(maxSingleExpense), budgetPeriod, Optional.ofNullable(description)
        )) {
            return oldBudget;
        }
        if (!title.equals(oldBudget.budgetDetails().title())) {
            title = duplicateBudgetTitleCheck(title, userId);
        }
        if (maxSingleExpense.compareTo(limit) > 0) {
            maxSingleExpense = limit;
        }

        updateHistoryChange(oldBudget);

        return budgetRepository.save(Budget.newOf(
                budgetId,
                userId,
                BudgetDetails.newOf(
                        title,
                        limit,
                        budgetType,
                        maxSingleExpense,
                        oldBudget.budgetDetails().historyOfChanges(),
                        budgetPeriod,
                        description == null ? "" : description
                )
        ));
    }

    @Override
    public void deleteBudgetByBudgetId(BudgetIdWrapper budgetId) {
        expenseRepository.deleteAllByBudgetId(budgetId);
        budgetRepository.deleteById(budgetId);
    }

    private void updateHistoryChange(Budget oldBudget) {
        TreeMap<Integer, LocalDateTime> history = oldBudget.budgetDetails().historyOfChanges();
        Integer newRecordNumber = history.lastEntry().getKey() + 1;
        history.put(newRecordNumber, LocalDateTime.now());
    }

    private boolean noParamChangeCheck(Budget oldBudget,
                                       Optional<String> newTitle,
                                       Optional<BigDecimal> newLimit,
                                       Optional<BudgetType> newBudgetType,
                                       Optional<BigDecimal> newMaxSingleExpense,
                                       BudgetPeriod newBudgetPeriod,
                                       Optional<String> newDescription
    ) {
        BudgetDetails oldBudgetDetails = oldBudget.budgetDetails();
        if (newTitle.isPresent() && !oldBudgetDetails.title().equals(newTitle.get())) return false;
        if (newLimit.isPresent() && !oldBudgetDetails.limit().equals(newLimit.get())) return false;
        if (newBudgetType.isPresent() && !oldBudgetDetails.budgetType().equals(newBudgetType.get()))
            return false;
        if (newMaxSingleExpense.isPresent() && !oldBudgetDetails.maxSingleExpense().equals(newMaxSingleExpense.get()))
            return false;
        if (newDescription.isPresent() && !oldBudgetDetails.description().equals(newDescription.get()))
            return false;
        if (!oldBudgetDetails.budgetPeriod().equals(newBudgetPeriod))
            return false;

        return true;
    }

    private String duplicateBudgetTitleCheck(String title, UserIdWrapper userId) {
        if (budgetRepository.existsByUserIdAndBudgetDetails_Title(userId, title)) {
            long counter = 0;
            StringBuilder stringBuilder = new StringBuilder(title);
            while (budgetRepository.existsByUserIdAndBudgetDetails_Title(userId, stringBuilder.toString())) {
                counter++;
                stringBuilder = new StringBuilder(title);
                stringBuilder.append("(").append(counter).append(")");
            }
            return stringBuilder.toString();
        }
        return title;
    }

    private BudgetPeriod getBudgetPeriod(LocalDate startTime, LocalDate endTime) {
        if (startTime == null) {
            LocalDate now = LocalDate.now();
            startTime = LocalDate.of(now.getYear(), now.getMonth().getValue(), 1);
        }
        if (endTime == null) {
            LocalDate now = LocalDate.now();
            endTime = LocalDate.of(now.getYear(), now.getMonth().getValue(), now.lengthOfMonth());
        }

        return BudgetPeriod.newOf(startTime, endTime);
    }

    private BigDecimal totalExpensesValueSum(Budget budget) {
        return expenseRepository.findAllByBudgetId(budget.budgetId())
                                .stream()
                                .map(Expense::expenseDetails)
                                .map(ExpenseDetails::amount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal budgetFullFillPercentage(BigDecimal base, BigDecimal actual) {
        return actual.multiply(BigDecimal.valueOf(100)).divide(base, 1, RoundingMode.DOWN);
    }

    private String getLimitFromBudget(Budget budget) {
        BudgetType ourBudgetType = budget.budgetDetails().budgetType();
        BigDecimal limit = budget.budgetDetails().limit().multiply(ourBudgetType.getValue());
        if (!ourBudgetType.getValue().equals(BigDecimal.valueOf(-1))) {
            return limit.toString();
        } else {
            return "no limit";
        }
    }
}
