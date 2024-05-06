package com.example.final_project.budget.model;

import com.example.final_project.currencyapi.model.MKTCurrency;

import java.math.BigDecimal;
import java.util.HashMap;

public record ExpenseSet(HashMap<MKTCurrency, BigDecimal> expenseMap) {

    public static ExpenseSet newOf(MKTCurrency currency) {
        HashMap<MKTCurrency, BigDecimal> newExpenseMap = new HashMap<>();
        newExpenseMap.put(currency, BigDecimal.ZERO);
        return new ExpenseSet(newExpenseMap);
    }

    public void add(MKTCurrency currency, BigDecimal expenseAmount) {
        if (!expenseMap.containsKey(currency)) expenseMap.put(currency, BigDecimal.ZERO);
        BigDecimal newExpenseAmount = expenseMap.get(currency).add(expenseAmount);
        this.expenseMap.put(currency, newExpenseAmount);
    }

    public void subtract(MKTCurrency currency, BigDecimal expenseAmount) {
        BigDecimal newExpenseSet = expenseMap.get(currency).subtract(expenseAmount);
        this.expenseMap.put(currency, newExpenseSet);
    }
}
