package com.example.final_project.domain.budgets;

import com.example.final_project.api.responses.BudgetStatusDTO;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
import com.example.final_project.infrastructure.exprepo.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class DefaultBudgetService implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final Supplier<BudgetId> budgetIdSupplier;

    public DefaultBudgetService(BudgetRepository budgetRepository, ExpenseRepository expenseRepository, Supplier<BudgetId> budgetIdSupplier) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.budgetIdSupplier = budgetIdSupplier;
    }

    @Override
    public Budget registerNewBudget(String title, BigDecimal limit, TypeOfBudget typeOfBudget, BigDecimal maxSingleExpense, String userId) {
        Budget budget = new Budget(budgetIdSupplier.get(), title, limit, typeOfBudget, maxSingleExpense, userId, LocalDateTime.now());
        budgetRepository.save(budget);
        return budget;
    }

    @Override
    public Optional<Budget> getBudgetById(BudgetId budgetId, String userId) {
        return budgetRepository.findBudgetByBudgetIdAndUserId(budgetId, userId);
    }

    @Override
    public void deleteBudgetById(BudgetId budgetId, String userId) {
        budgetRepository.deleteBudgetByBudgetIdAndUserId(budgetId, userId);
    }

    @Override
    public Optional<Budget> updateBudgetContent(BudgetId budgetId,
                                                Optional<String> title,
                                                Optional<BigDecimal> limit,
                                                Optional<TypeOfBudget> typeOfBudget,
                                                Optional<BigDecimal> maxSingleExpense,
                                                String userId,
                                                Optional<LocalDateTime> timestamp
    ) {
        budgetRepository.findBudgetByBudgetIdAndUserId(budgetId, userId).map(
                budgetFromRepository -> new Budget(budgetId,
                        title.orElseGet(budgetFromRepository::title),
                        limit.orElseGet(budgetFromRepository::limit),
                        typeOfBudget.orElseGet(budgetFromRepository::typeOfBudget),
                        maxSingleExpense.orElseGet(budgetFromRepository::maxSingleExpense),
                        userId,
                        timestamp.orElseGet(budgetFromRepository::timestamp)
                )).ifPresent(budgetRepository::save);
        return budgetRepository.findBudgetByBudgetIdAndUserId(budgetId, userId);
    }


    @Override
    public List<Budget> getBudgets(String userId) {
        return budgetRepository.findAllByUserId(userId);
    }

    @Override
    public Budget updateBudgetById(BudgetId budgetId,
                                   String title,
                                   BigDecimal limit,
                                   TypeOfBudget typeOfBudget,
                                   BigDecimal maxSingleExpense,
                                   String userId,
                                   LocalDateTime timestamp) {

        return budgetRepository.save(new Budget(
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
    public Page<Budget> findAllByPage(String userId, Pageable pageable) {
        return budgetRepository.findAllByUserId(userId, pageable);
    }

    private BigDecimal totalExpensesValue(BudgetId budgetId, String userId) {
        return expenseRepository.findExpensesByBudgetIdAndUserId(budgetId, userId)
                .stream()
                .map(Expense::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal budgetFullFillPerc(BigDecimal base, BigDecimal actual) {
        return actual.multiply(BigDecimal.valueOf(100)).divide(base, 1, RoundingMode.DOWN);
    }

    @Override
    public BudgetStatusDTO getBudgetStatus(BudgetId budgetId, String userId) {

        Optional<Budget> budget = budgetRepository.findBudgetByBudgetIdAndUserId(budgetId, userId);
        BigDecimal amountLeft = budget.get().limit().subtract(totalExpensesValue(budgetId, userId));
        BigDecimal budgetFullFillPerc = budgetFullFillPerc(budget.get().limit(), totalExpensesValue(budgetId, userId));
        Integer totalExpNumb = expenseRepository.findExpenseByBudgetIdAndUserId(budgetId, userId).size();
        String limitValue = getLimitFromBudget(budget.get());
        return BudgetStatusDTO.newOf(budgetId.toString(), totalExpNumb,
                totalExpensesValue(budgetId, userId), amountLeft,
                budgetFullFillPerc, budget.get().typeOfBudget().getTitle(), limitValue, LocalDateTime.now());
    }

    public String getLimitFromBudget(Budget budget) {
        BigDecimal limit = budget.limit().multiply(budget.typeOfBudget().getValue());

        if (!budget.typeOfBudget().getValue().equals(BigDecimal.valueOf(-1))) {
            return limit.toString();
        } else {
            return "no limit";
        }
    }

}
