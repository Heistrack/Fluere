package com.example.final_project.domain.budgets;

import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class DefaultBudgetService implements BudgetService {

    private final BudgetRepository budgetRepository;

    private final Supplier<BudgetId> budgetIdSupplier;

    public DefaultBudgetService(BudgetRepository budgetRepository, Supplier<BudgetId> budgetIdSupplier) {
        this.budgetRepository = budgetRepository;
        this.budgetIdSupplier = budgetIdSupplier;
    }


    @Override
    public Budget registerNewBudget(String title, BigDecimal limit, TypeOfBudget typeOfBudget, BigDecimal maxSingleExpense, String userId) {
        Budget budget = new Budget(budgetIdSupplier.get(), title, limit, typeOfBudget, maxSingleExpense, userId);
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
                                                String userId
    ) {
        budgetRepository.findBudgetByBudgetIdAndUserId(budgetId, userId).map(
                budgetFromRepository -> new Budget(budgetId,
                        title.orElse(budgetFromRepository.title()),
                        limit.orElse(budgetFromRepository.limit()),
                        typeOfBudget.orElse(budgetFromRepository.typeOfBudget()),
                        maxSingleExpense.orElse(budgetFromRepository.maxSingleExpense()),
                        userId
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
                                   String userId) {

        return budgetRepository.save(new Budget(
                budgetId,
                title,
                limit,
                typeOfBudget,
                maxSingleExpense,
                userId
                ));
    }

    @Override
    public Page<Budget> findAllByPage(String userId, Pageable pageable) {
        return budgetRepository.findAllByUserId(userId, pageable);
    }


}
