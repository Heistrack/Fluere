package com.example.final_project.domain.budgets;

import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
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
    public Budget registerNewBudget(String title, BigDecimal limit, TypeOfBudget typeOfBudget, BigDecimal maxSingleExpense) {
        Budget budget = new Budget(budgetIdSupplier.get(), title, limit, typeOfBudget, maxSingleExpense);
        budgetRepository.save(budget);
        return budget;
    }

    @Override
    public Optional<Budget> getBudgetById(BudgetId budgetId) {
        return budgetRepository.findBudgetByBudgetId(budgetId);
    }

    @Override
    public void deleteBudgetById(BudgetId budgetId) {
        budgetRepository.deleteById(budgetId);
    }

    @Override
    public Optional<Budget> updateBudgetContent(BudgetId budgetId,
                                                Optional<String> title,
                                                Optional<BigDecimal> limit,
                                                Optional<TypeOfBudget> typeOfBudget,
                                                Optional<BigDecimal> maxSingleExpense
    ) {
        budgetRepository.findBudgetByBudgetId(budgetId).map(
                budgetFromRepository -> new Budget(budgetId,
                        title.orElse(budgetFromRepository.title()),
                        limit.orElse(budgetFromRepository.limit()),
                        typeOfBudget.orElse(budgetFromRepository.typeOfBudget()),
                        maxSingleExpense.orElse(budgetFromRepository.maxSingleExpense())
                        )).ifPresent(budgetRepository::save);
        return budgetRepository.findBudgetByBudgetId(budgetId);
    }

    public List<Budget> getBudgets() {
        return budgetRepository.findAll();
    }

    @Override
    public Budget updateBudgetById(BudgetId budgetId,
                                   String title,
                                   BigDecimal limit,
                                   TypeOfBudget typeOfBudget,
                                   BigDecimal maxSingleExpense) {

        return budgetRepository.save(new Budget(
                budgetId,
                title,
                limit,
                typeOfBudget,
                maxSingleExpense
                ));
    }
}
