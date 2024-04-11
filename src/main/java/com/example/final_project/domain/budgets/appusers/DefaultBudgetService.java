package com.example.final_project.domain.budgets.appusers;

import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseDetails;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
import com.example.final_project.infrastructure.exprepo.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class DefaultBudgetService implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final Supplier<BudgetIdWrapper> budgetIdSupplier;
    private final JwtService jwtService;

    @Override
    public Budget registerNewBudget(String title, BigDecimal limit, BudgetType budgetType,
                                    BigDecimal maxSingleExpense, String description, Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        String checkedTitle = duplicateBudgetTitleCheck(title, userId);

        TreeMap<Integer, LocalDateTime> historyOfChange = new TreeMap<>();
        historyOfChange.put(1, LocalDateTime.now());

        if (maxSingleExpense.compareTo(limit) > 0) {
            maxSingleExpense = limit;
        }

        Budget budget = Budget.newOf(
                budgetIdSupplier.get(), userId,
                BudgetDetails.newOf(checkedTitle, limit,
                                    budgetType, maxSingleExpense,
                                    historyOfChange,
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
    public Page<Budget> getAllByPage(Authentication authentication, Pageable pageable) {
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
    public Budget patchBudgetContent(BudgetIdWrapper budgetId,
                                     Optional<String> title,
                                     Optional<BigDecimal> limit,
                                     Optional<BudgetType> budgetType,
                                     Optional<BigDecimal> maxSingleExpense,
                                     Optional<String> description,
                                     Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Budget oldBudget = budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                                           .orElseThrow(() -> new NoSuchElementException(
                                                   "Can't update budget, because it doesn't exist"));

        if (noParamChangeCheck(oldBudget, title, limit, budgetType, maxSingleExpense, description)) {
            return oldBudget;
        }
        if (title.isPresent() && !title.get().equals(oldBudget.budgetDetails().title())) {
            title = Optional.of(duplicateBudgetTitleCheck(title.get(), userId));
        }
        if (maxSingleExpense.isPresent() && maxSingleExpense.get().compareTo(
                limit.orElse(oldBudget.budgetDetails().limit())) > 0) {
            maxSingleExpense = limit;
        }

        updateHistoryChange(oldBudget);

        Optional<BigDecimal> checkedMaxSingleExpense = maxSingleExpense;
        Optional<String> checkedTitle = title;

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
                                oldBudget.budgetDetails().historyOfChanges(),
                                description.orElseGet(() -> budgetFromRepository.budgetDetails().description())
                        )
                )).orElseThrow(IllegalArgumentException::new));
    }

    @Override
    public Budget updateBudgetById(BudgetIdWrapper budgetId,
                                   String title,
                                   BigDecimal limit,
                                   BudgetType budgetType,
                                   BigDecimal maxSingleExpense,
                                   Optional<String> description,
                                   Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Budget oldBudget = budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                                           .orElseThrow(() -> new NoSuchElementException(
                                                   "Can't update budget, because it doesn't exist"));

        if (noParamChangeCheck(oldBudget, Optional.of(title), Optional.of(limit), Optional.of(budgetType),
                               Optional.of(maxSingleExpense), description
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
                        description.orElse("")
                )
        ));
    }

    @Override
    public void deleteAllBudgetExpensesByBudgetId(BudgetIdWrapper budgetId, Authentication authentication) {
        BudgetIdWrapper checkedBudgetId = getBudgetById(budgetId, authentication).budgetId();
        expenseRepository.deleteAllByBudgetId(checkedBudgetId);
        budgetRepository.deleteById(budgetId);
    }

    private boolean noParamChangeCheck(Budget oldBudget,
                                       Optional<String> newTitle,
                                       Optional<BigDecimal> newLimit,
                                       Optional<BudgetType> newBudgetType,
                                       Optional<BigDecimal> newMaxSingleExpense,
                                       Optional<String> newDescription
    ) {
        if (newTitle.isPresent() && !oldBudget.budgetDetails().title().equals(newTitle.get())) return false;
        if (newLimit.isPresent() && !oldBudget.budgetDetails().limit().equals(newLimit.get())) return false;
        if (newBudgetType.isPresent() && !oldBudget.budgetDetails().budgetType().equals(newBudgetType.get()))
            return false;
        if (newMaxSingleExpense.isPresent() && !oldBudget.budgetDetails().maxSingleExpense()
                                                         .equals(newMaxSingleExpense.get())) return false;
        if (newDescription.isPresent() && !oldBudget.budgetDetails().description().equals(newDescription.get()))
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

    private void updateHistoryChange(Budget oldBudget) {
        TreeMap<Integer, LocalDateTime> history = oldBudget.budgetDetails().historyOfChanges();
        Integer newRecordNumber = history.lastEntry().getKey() + 1;
        history.put(newRecordNumber, LocalDateTime.now());
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
