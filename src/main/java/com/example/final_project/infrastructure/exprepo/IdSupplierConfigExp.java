package com.example.final_project.infrastructure.exprepo;

import com.example.final_project.domain.expenses.ExpenseId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Supplier;

@Configuration
public class IdSupplierConfigExp {

    @Bean
    public Supplier<ExpenseId> expenseIdSupplier(){
        return () -> new ExpenseId(UUID.randomUUID().toString());
    }
}
