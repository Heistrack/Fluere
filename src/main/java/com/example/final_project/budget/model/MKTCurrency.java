package com.example.final_project.budget.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum MKTCurrency {
    USD(BigDecimal.ONE),
    GBP(BigDecimal.ONE),
    PLN(BigDecimal.ONE),
    EUR(BigDecimal.ONE),
    BTC(BigDecimal.ONE),
    CHF(BigDecimal.ONE),
    JPY(BigDecimal.ONE),
    AUD(BigDecimal.ONE),
    CAD(BigDecimal.ONE),
    SGD(BigDecimal.ONE),
    RUB(BigDecimal.ONE);
    //FIXME I want to use currency as a key in expenseMap in budget and as a converter
    // currencies would be transfered to desireable currency by using external API cantor
    private final BigDecimal value;
}
