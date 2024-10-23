package com.example.fluere.currencyapi.repository;


import com.example.fluere.currencyapi.model.FiatCurrencyDailyData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface CurrencyRepository extends MongoRepository<FiatCurrencyDailyData, UUID> {
}
