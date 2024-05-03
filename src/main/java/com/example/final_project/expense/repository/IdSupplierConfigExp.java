package com.example.final_project.expense.repository;

import com.example.final_project.expense.service.ExpenseIdWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Supplier;

@Configuration
class IdSupplierConfigExp {

    @Bean
    public Supplier<ExpenseIdWrapper> expenseIdSupplier() {
        return () -> new ExpenseIdWrapper(UUID.randomUUID());
    }
}
