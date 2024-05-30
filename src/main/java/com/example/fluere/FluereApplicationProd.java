package com.example.fluere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Profile("prod")
@EnableMongoRepositories
@SpringBootApplication
public class FluereApplicationProd {
    public static void main(String[] args) {
        SpringApplication.run(FluereApplicationProd.class, args);
    }
}
