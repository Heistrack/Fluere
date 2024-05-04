package com.example.final_project.budget.model;


import com.example.final_project.userentity.model.UserIdWrapper;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;

@Document
public record Budget(
        @MongoId
        BudgetIdWrapper budgetId,
        UserIdWrapper userId,
        BudgetDetails budgetDetails
) {
    public static Budget newOf(BudgetIdWrapper budgetId, UserIdWrapper userId, BudgetDetails budgetDetails) {
        return new Budget(budgetId, userId, budgetDetails);
    }
    //TODO I don't know where to put update expenseMap method here or directly in the expenseMap. This approach make less boiler plate

    public void addBalance(MKTCurrency currency, BigDecimal expenseAmount) {
        this.budgetDetails.expenseSet().add(currency, expenseAmount);
    }

    public void subtractBalance(MKTCurrency currency, BigDecimal expenseAmount) {
        this.budgetDetails.expenseSet().subtract(currency, expenseAmount);
    }

    public BigDecimal showBalance(MKTCurrency currency) {
        return this.budgetDetails.expenseSet().sumBalance(currency);
    }
}
