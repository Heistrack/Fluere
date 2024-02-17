package com.example.final_project.domain.budgets;

import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.expenses.Expense;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
        BigDecimal amountLeft = budget.limit().subtract(moneySpend);
        BigDecimal budgetFullFillPercent = budgetFullFillPercentage(budget.limit(), moneySpend);
        Integer expensesNumber = expenseRepository.findAllByBudgetId(budgetId).size();
        String limitValue = getLimitFromBudget(budget);

        return BudgetStatusDTO.newOf(budgetId.id(), expensesNumber,
                                     moneySpend, amountLeft,
                                     budgetFullFillPercent, budget.typeOfBudget().getTitle(), budget.limit(),
                                     budget.maxSingleExpense(),
                                     LocalDateTime.now()
        );
    }

    @Override
    public List<Budget> getAllBudgetsByUserId(UserIdWrapper userId) {
        return budgetRepository.findAllByUserId(userId);
    }

    @Override
    public Budget patchBudgetContent(BudgetIdWrapper budgetId,
                                     Optional<String> title,
                                     Optional<BigDecimal> limit,
                                     Optional<TypeOfBudget> typeOfBudget,
                                     Optional<BigDecimal> maxSingleExpense,
                                     UserIdWrapper userId
    ) {
        Budget oldBudget = budgetRepository.findByBudgetIdAndUserId(budgetId, userId)
                                           .orElseThrow(() -> new NoSuchElementException(
                                                   "Can't update budget, because it doesn't exist"));

        if (title.isPresent()) {
            title = Optional.of(duplicateBudgetTitleCheck(title.get(), userId));
        }
        Optional<String> checkedTitle = title;

        Optional.of(oldBudget).map(
                budgetFromRepository -> Budget.newOf(
                        budgetId,
                        checkedTitle.orElseGet(budgetFromRepository::title),
                        limit.orElseGet(budgetFromRepository::limit),
                        typeOfBudget.orElseGet(budgetFromRepository::typeOfBudget),
                        maxSingleExpense.orElseGet(budgetFromRepository::maxSingleExpense),
                        userId,
                        budgetFromRepository.registerTime()
                )).ifPresent(budgetRepository::save);
        return budgetRepository.findByBudgetIdAndUserId(budgetId, userId).get();
    }

    @Override
    public Budget registerNewBudget(String title, BigDecimal limit, TypeOfBudget typeOfBudget,
                                    BigDecimal maxSingleExpense, UserIdWrapper userId
    ) {
        title = duplicateBudgetTitleCheck(title, userId);
        Budget budget = new Budget(
                budgetIdSupplier.get(), title, limit, typeOfBudget, maxSingleExpense, userId, LocalDateTime.now());
        budgetRepository.save(budget);
        return budget;
    }


    @Override
    public Budget updateBudgetById(BudgetIdWrapper budgetId,
                                   String title,
                                   BigDecimal limit,
                                   TypeOfBudget typeOfBudget,
                                   BigDecimal maxSingleExpense,
                                   UserIdWrapper userId,
                                   LocalDateTime timestamp
    ) {
        if (!budgetRepository.existsByBudgetIdAndUserId(budgetId, userId)) {
            throw new NoSuchElementException("Can't update budget, because it doesn't exist");
        }
        title = duplicateBudgetTitleCheck(title, userId);

        return budgetRepository.save(Budget.newOf(
                budgetId,
                title,
                limit,
                typeOfBudget,
                maxSingleExpense,
                userId,
                timestamp
        ));
    }

    @Override
    public Page<Budget> findAllByPage(UserIdWrapper userId, Pageable pageable) {

        Page<Budget> allByUserId = budgetRepository.findAllByUserId(userId, pageable);
        if (allByUserId.isEmpty()) throw new NoSuchElementException("No results match");
        return allByUserId;
    }

    private String duplicateBudgetTitleCheck(String title, UserIdWrapper userId) {
        if (budgetRepository.existsByTitleAndUserId(title, userId)) {
            long counter = 0;
            StringBuilder stringBuilder = new StringBuilder();
            while (budgetRepository.existsByTitleAndUserId(stringBuilder.toString(), userId)) {
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
                                .map(Expense::amount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal budgetFullFillPercentage(BigDecimal base, BigDecimal actual) {
        return actual.multiply(BigDecimal.valueOf(100)).divide(base, 1, RoundingMode.DOWN);
    }

    private String getLimitFromBudget(Budget budget) {
        BigDecimal limit = budget.limit().multiply(budget.typeOfBudget().getValue());
        if (!budget.typeOfBudget().getValue().equals(BigDecimal.valueOf(-1))) {
            return limit.toString();
        } else {
            return "no limit";
        }
    }
}
