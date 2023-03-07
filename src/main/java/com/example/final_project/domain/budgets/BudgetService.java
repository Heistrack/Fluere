package com.example.final_project.domain.budgets;

import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseId;
import com.example.final_project.infrastructure.bdtrepo.BudgetRepository;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BudgetService {

    Budget registerNewBudget(String title, BigDecimal limit, TypeOfBudget typeOfBudget, BigDecimal maxSingleExpense, String userId);
    Optional<Budget> getBudgetById(BudgetId budgetId, String userId);

    void deleteBudgetById(BudgetId budgetId, String userId);
    Optional<Budget> updateBudgetContent(BudgetId budgetId, Optional<String> title, Optional<BigDecimal> limit,
                                         Optional<TypeOfBudget> typeOfBudget, Optional<BigDecimal> maxSingleExpense, String userId);
    List<Budget> getBudgets(String userId);

    Budget updateBudgetById(BudgetId BudgetId, String title, BigDecimal limit, TypeOfBudget typeOfBudget,
                            BigDecimal maxSingleExpense, String userId);

    Page<Budget> findAllByPage(String userId, Pageable pageable);
}
