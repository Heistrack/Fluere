package com.example.final_project.infrastructure.bdtrepo;

import com.example.final_project.domain.budgets.BudgetId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Supplier;

@Configuration
public class IdSupplierConfigBud {
    @Bean
    public Supplier<BudgetId> budgetIdSupplier() {
        return () -> BudgetId.newOf(UUID.randomUUID().toString());
    }
}
