package com.example.final_project.budget.response;

import com.example.final_project.budget.model.*;
import com.example.final_project.currencyapi.model.MKTCurrency;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class BudgetResponseDTO
        extends RepresentationModel<BudgetResponseDTO>
        implements LinkableDTO {

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


    public static BudgetResponseDTO fromDomain(Budget budget) {
        return new BudgetResponseDTO(
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


    @Override
    public void addLink(Link link) {
        this.add(link);
    }

    @Override
    public String PathMessage() {
        return budgetId.toString();
    }
}
