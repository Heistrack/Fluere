package com.example.fluere;

import com.example.fluere.bootloader.MongoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Profile({"dev", "test"})
@EnableMongoRepositories
@SpringBootApplication
public class FluereApplicationDev {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(FluereApplicationDev.class, args);
        MongoService mongoService = context.getBean(MongoService.class);
        mongoService.performConnectivityCheck();
    }
}
