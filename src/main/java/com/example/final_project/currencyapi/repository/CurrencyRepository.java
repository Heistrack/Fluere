package com.example.final_project.currencyapi.repository;


import com.example.final_project.currencyapi.model.FiatCurrencyDailyData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface CurrencyRepository extends MongoRepository<FiatCurrencyDailyData, UUID> {
}
