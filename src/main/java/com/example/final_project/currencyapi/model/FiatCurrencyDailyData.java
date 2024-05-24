package com.example.final_project.currencyapi.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

@Document
public record FiatCurrencyDailyData(
        @MongoId
        UUID currencyDataId,
        //todo update how much request left cuz it doesn't work
        Integer howMuchRequestLeftAPI,
        LocalDate previousDateCheck,
        HashMap<MKTCurrency, BigDecimal> conversionRates
) {
}
