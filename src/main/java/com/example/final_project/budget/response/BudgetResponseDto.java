package com.example.final_project.budget.response;

import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.BudgetPeriod;
import com.example.final_project.budget.model.BudgetType;
import com.example.final_project.budget.model.ExpenseSet;
import com.example.final_project.budget.repository.BudgetRepository;
import com.example.final_project.currencyapi.model.MKTCurrency;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.UUID;

@Getter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BudgetResponseDto extends RepresentationModel<BudgetResponseDto> {

        private final UUID budgetId;
        private final String title;
        private final BigDecimal limit;
        private final BudgetType budgetType;
        private final BigDecimal maxSingleExpense;
        private final MKTCurrency defaultCurrency;
        private final ExpenseSet expenseSet;
        private final TreeMap<Integer, LocalDateTime> historyOfChanges;
        private final BudgetPeriod budgetPeriod;
        private final String description;


    public static BudgetResponseDto fromDomain(Budget budget) {
        return new BudgetResponseDto(
                budget.budgetId().id(),
                budget.budgetDetails().title(),
                budget.budgetDetails().limit(),
                budget.budgetDetails().budgetType(),
                budget.budgetDetails().maxSingleExpense(),
                budget.budgetDetails().defaultCurrency(),
                budget.budgetDetails().expenseSet(),
                budget.budgetDetails().historyOfChanges(),
                budget.budgetDetails().budgetPeriod(),
                budget.budgetDetails().description()
        );
    }
}
