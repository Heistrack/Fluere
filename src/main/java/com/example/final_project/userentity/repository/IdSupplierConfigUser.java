package com.example.final_project.userentity.repository;


import com.example.final_project.userentity.model.UserIdWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Supplier;


@Configuration
class IdSupplierConfigUser {
    @Bean
    public Supplier<UserIdWrapper> userIdSupplier() {
        return () -> new UserIdWrapper(UUID.randomUUID());
    }
}
