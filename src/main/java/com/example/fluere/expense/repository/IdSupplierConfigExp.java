package com.example.fluere.expense.repository;

import com.example.fluere.expense.model.ExpenseIdWrapper;
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
