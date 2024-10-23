package com.example.fluere.budget.model;


import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.userentity.model.UserIdWrapper;
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

    public void addBalance(MKTCurrency currency, BigDecimal expenseAmount) {
        this.budgetDetails.expenseSet().add(currency, expenseAmount);
    }

    public void subtractBalance(MKTCurrency currency, BigDecimal expenseAmount) {
        this.budgetDetails.expenseSet().subtract(currency, expenseAmount);
    }
}
