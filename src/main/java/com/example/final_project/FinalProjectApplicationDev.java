package com.example.final_project;

import com.example.final_project.bootloader.MongoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Profile("dev")
@EnableMongoRepositories
@SpringBootApplication
public class FinalProjectApplicationDev {
    public static void main(String[] args) {
        //TODO remove this connectivity check lines
        ConfigurableApplicationContext context = SpringApplication.run(FinalProjectApplicationDev.class, args);
        MongoService mongoService = context.getBean(MongoService.class);
        mongoService.performConnectivityCheck();
    }
}
