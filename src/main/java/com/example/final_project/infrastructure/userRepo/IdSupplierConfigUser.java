package com.example.final_project.infrastructure.userRepo;


import com.example.final_project.domain.users.UserId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.UUID;
import java.util.function.Supplier;


@Configuration
public class IdSupplierConfigUser {

    @Bean
    public Supplier<UserId> userIdSupplier() {
        return () -> new UserId(UUID.randomUUID().toString());
    }
}
