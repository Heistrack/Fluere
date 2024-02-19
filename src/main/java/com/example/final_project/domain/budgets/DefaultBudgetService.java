package com.example.final_project.domain.budgets;

import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseDetails;
import com.example.final_project.domain.expenses.ExpenseType;
import com.example.final_project.domain.users.UserIdWrapper;
import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
import com.example.final_project.infrastructure.exprepo.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class DefaultBudgetService implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final Supplier<BudgetIdWrapper> budgetIdSupplier;

    @Override
    public Budget getBudgetById(BudgetIdWrapper budgetId, UserIdWrapper userId) {
        return budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                               .orElseThrow(() -> new NoSuchElementException("There's no such budget."));
    }

    @Override
    public void deleteBudgetByBudgetId(BudgetIdWrapper budgetId) {
        expenseRepository.deleteAllByBudgetId(budgetId);
        budgetRepository.deleteByBudgetId(budgetId);
    }

    @Override
    public BudgetStatusDTO getBudgetStatus(BudgetIdWrapper budgetId, UserIdWrapper userId) {
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
                                     budget.budgetDetails().limit(),
                                     budget.budgetDetails().maxSingleExpense(),
                                     LocalDateTime.now()
        );
    }

    @Override
    public List<Budget> getAllBudgetsByUserId(UserIdWrapper userId) {
        return budgetRepository.findAllByUserId(userId);
    }

    @Override
    public Budget registerNewBudget(String title, BigDecimal limit, BudgetType budgetType,
                                    BigDecimal maxSingleExpense, UserIdWrapper userId
    ) {
        String checkedTitle = duplicateBudgetTitleCheck(title, userId);

        TreeMap<Integer, LocalDateTime> historyOfChange = new TreeMap<>();
        historyOfChange.put(1, LocalDateTime.now());

        Budget budget = Budget.newOf(
                budgetIdSupplier.get(), userId,
                BudgetDetails.newOf(checkedTitle, limit,
                                    budgetType, maxSingleExpense, historyOfChange
                )
        );
        return budgetRepository.save(budget);
    }

    @Override
    public Budget patchBudgetContent(BudgetIdWrapper budgetId,
                                     Optional<String> title,
                                     Optional<BigDecimal> limit,
                                     Optional<BudgetType> budgetType,
                                     Optional<BigDecimal> maxSingleExpense,
                                     UserIdWrapper userId
    ) {
        Budget oldBudget = budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                                           .orElseThrow(() -> new NoSuchElementException(
                                                   "Can't update budget, because it doesn't exist"));

        if (noParamChangeCheck(oldBudget, title, limit, budgetType,
                               maxSingleExpense)) {
            return oldBudget;
        }

        if (title.isPresent() && !title.get().equals(oldBudget.budgetDetails().title())) {
            title = Optional.of(duplicateBudgetTitleCheck(title.get(), userId));
        }
        Optional<String> checkedTitle = title;

        BudgetDetails ourBudgetDetails = oldBudget.budgetDetails();
        Integer newRecordNumber = ourBudgetDetails.historyOfChanges().lastEntry().getKey() + 1;
        ourBudgetDetails.historyOfChanges().put(newRecordNumber, LocalDateTime.now());

        return budgetRepository.save(budgetRepository.findByBudgetIdAndUserId(budgetId, userId).map(
                budgetFromRepository -> Budget.newOf(
                        budgetId,
                        userId,
                        BudgetDetails.newOf(
                                checkedTitle.orElseGet(() -> budgetFromRepository.budgetDetails().title()),
                                limit.orElseGet(() -> budgetFromRepository.budgetDetails().limit()),
                                budgetType.orElseGet(() -> budgetFromRepository.budgetDetails().budgetType()),
                                maxSingleExpense.orElseGet(
                                        () -> budgetFromRepository.budgetDetails().maxSingleExpense()),
                                ourBudgetDetails.historyOfChanges()
                        )
                )).orElseThrow(IllegalArgumentException::new));
    }


    @Override
    public Budget updateBudgetById(BudgetIdWrapper budgetId,
                                   String title,
                                   BigDecimal limit,
                                   BudgetType budgetType,
                                   BigDecimal maxSingleExpense,
                                   UserIdWrapper userId
    ) {
        Budget oldBudget = budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                                           .orElseThrow(() -> new NoSuchElementException(
                                                   "Can't update budget, because it doesn't exist"));

        if (noParamChangeCheck(oldBudget, Optional.of(title), Optional.of(limit), Optional.of(budgetType),
                               Optional.of(maxSingleExpense))) {
            return oldBudget;
        }

        String checkedTitle = "";

        if (!title.equals(oldBudget.budgetDetails().title())) {
            checkedTitle = duplicateBudgetTitleCheck(title, userId);
        }

        BudgetDetails ourBudgetDetails = oldBudget.budgetDetails();
        Integer newRecordNumber = ourBudgetDetails.historyOfChanges().lastEntry().getKey() + 1;
        ourBudgetDetails.historyOfChanges().put(newRecordNumber, LocalDateTime.now());

        return budgetRepository.save(Budget.newOf(
                budgetId,
                userId,
                BudgetDetails.newOf(
                        checkedTitle,
                        limit,
                        budgetType,
                        maxSingleExpense,
                        ourBudgetDetails.historyOfChanges()
                )
        ));
    }

    private boolean noParamChangeCheck(Budget oldBudget, Optional<String> title,
                                       Optional<BigDecimal> limit,
                                       Optional<BudgetType> budgetType,
                                       Optional<BigDecimal> maxSingleExpense
    ) {
        String newTitle = title.orElse(oldBudget.budgetDetails().title());
        BigDecimal newLimit = limit.orElse(oldBudget.budgetDetails().limit());
        BudgetType newBudgetType = budgetType.orElse(oldBudget.budgetDetails().budgetType());
        BigDecimal newMaxSingleExpense = maxSingleExpense.orElse(oldBudget.budgetDetails().maxSingleExpense());

        return Objects.equals(oldBudget.budgetDetails().title(), newTitle) &&
                Objects.equals(oldBudget.budgetDetails().limit(), newLimit) &&
                Objects.equals(oldBudget.budgetDetails().budgetType(), newBudgetType) &&
                Objects.equals(oldBudget.budgetDetails().maxSingleExpense(), newMaxSingleExpense);
    }

    @Override
    public Page<Budget> findAllByPage(UserIdWrapper userId, Pageable pageable) {

        Page<Budget> allByUserId = budgetRepository.findAllByUserId(userId, pageable);

        if (allByUserId.isEmpty()) throw new NoSuchElementException("No results match");
        return allByUserId;
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
