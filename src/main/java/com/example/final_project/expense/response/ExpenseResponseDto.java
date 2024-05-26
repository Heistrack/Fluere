package com.example.final_project.expense.response;


import com.example.final_project.budget.model.LinkableDTO;
import com.example.final_project.currencyapi.model.MKTCurrency;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

@Data
@EqualsAndHashCode(callSuper = false)
public class ExpenseResponseDto extends RepresentationModel<ExpenseResponseDto> implements LinkableDTO {

    private final String title;
    private final String expenseId;
    private final String budgetId;
    private final BigDecimal amount;
    private final MKTCurrency currency;
    private final TreeMap<Integer, LocalDateTime> historyOfChanges;
    private final ExpenseType expenseType;
    private final String description;

    public static ExpenseResponseDto fromDomain(Expense expense) {
        return new ExpenseResponseDto(
                expense.expenseDetails().title(),
                expense.expenseId().id().toString(),
                expense.budgetId().id().toString(),
                expense.expenseDetails().amount(),
                expense.expenseDetails().currency(),
                expense.expenseDetails().historyOfChanges(),
                expense.expenseDetails().expenseType(),
                expense.expenseDetails().description()
        );
    }

    @Override
    public void addLink(Link link) {
        this.add(link);
    }

    @Override
    public String PathMessage() {
        return expenseId;
    }
}
