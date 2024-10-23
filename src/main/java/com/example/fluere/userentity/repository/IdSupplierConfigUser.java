package com.example.fluere.userentity.repository;


import com.example.fluere.userentity.model.UserIdWrapper;
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
