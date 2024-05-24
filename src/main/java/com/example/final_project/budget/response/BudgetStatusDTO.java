package com.example.final_project.budget.response;

import com.example.final_project.budget.model.BudgetDetails;
import com.example.final_project.budget.model.LinkableDTO;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class BudgetStatusDTO extends RepresentationModel<BudgetStatusDTO> implements LinkableDTO {

    private final UUID budgetId;
    private final BudgetDetails budgetDetails;
    private final BigDecimal amountLeft;
    private final BigDecimal totalMoneySpent;
    private final Integer totalExpensesNumber;
    private final Float budgetFullFillPercent;
    private final String trueLimitValue;
    private final TreeMap<LocalDate, List<Expense>> dayExpenses;
    private final HashMap<ExpenseType, List<Expense>> categoryExpenses;
    private final HashMap<ExpenseType, Float> categoryExpensesPercentage;

    //TODO remove that method and apply builder instead
    public static BudgetStatusDTO newOf(UUID budgetId,
                                        BudgetDetails budgetDetails,
                                        BigDecimal amountLeft,
                                        BigDecimal totalMoneySpent,
                                        Integer totalExpensesNumber,
                                        Float budgetFullFillPercent,
                                        String trueLimitValue,
                                        TreeMap<LocalDate, List<Expense>> dayExpenses,
                                        HashMap<ExpenseType, List<Expense>> categoryExpenses,
                                        HashMap<ExpenseType, Float> categoryExpensesPercentage
    ) {
        return new BudgetStatusDTO(
                budgetId,
                budgetDetails,
                amountLeft,
                totalMoneySpent,
                totalExpensesNumber,
                budgetFullFillPercent,
                trueLimitValue,
                dayExpenses,
                categoryExpenses,
                categoryExpensesPercentage
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
