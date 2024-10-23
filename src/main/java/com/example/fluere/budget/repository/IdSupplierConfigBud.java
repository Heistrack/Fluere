package com.example.fluere.budget.repository;

import com.example.fluere.budget.model.BudgetIdWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Supplier;

@Configuration
class IdSupplierConfigBud {
    @Bean
    public Supplier<BudgetIdWrapper> budgetIdSupplier() {
        return () -> BudgetIdWrapper.newOf(UUID.randomUUID());
    }
}
