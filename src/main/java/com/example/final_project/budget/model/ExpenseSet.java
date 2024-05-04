package com.example.final_project.budget.model;

import java.math.BigDecimal;
import java.util.HashMap;


public record ExpenseSet(HashMap<MKTCurrency, BigDecimal> expenseMap) {

    //TODO change expenseMap name to sumOfAll expenses
    public static ExpenseSet newOf(MKTCurrency currency) {
        HashMap<MKTCurrency, BigDecimal> newExpenseMap = new HashMap<>();
        newExpenseMap.put(currency, BigDecimal.ZERO);
        return new ExpenseSet(newExpenseMap);
    }

    public void add(MKTCurrency currency, BigDecimal expenseAmount) {
        BigDecimal newExpenseSet = expenseMap.get(currency).add(expenseAmount);
        this.expenseMap.put(currency, newExpenseSet);
    }

    public void subtract(MKTCurrency currency, BigDecimal expenseAmount) {
        BigDecimal newExpenseSet = expenseMap.get(currency).subtract(expenseAmount);
        this.expenseMap.put(currency, newExpenseSet);
    }

    public BigDecimal sumBalance(MKTCurrency currency) {
        //TODO add implementation of currency changing according to currency price
        return this.expenseMap.entrySet()
                              .stream()
                              .map(entry -> entry.getValue()
                                                 .multiply(entry.getKey()
                                                                .getValue()))
                              .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
