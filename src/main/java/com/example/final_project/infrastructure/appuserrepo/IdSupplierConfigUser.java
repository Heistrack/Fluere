package com.example.final_project.infrastructure.appuserrepo;


import com.example.final_project.domain.users.UserIdWrapper;
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
