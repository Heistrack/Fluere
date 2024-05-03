package com.example.final_project.budget.repository;

import com.example.final_project.budget.service.BudgetIdWrapper;
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
