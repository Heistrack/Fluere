package com.example.fluere;

import com.example.fluere.bootloader.MongoService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Profile("dev")
@EnableMongoRepositories
@SpringBootApplication
public class FluereApplicationDev {

    public static void main(String[] args) {
        //TODO remove this connectivity check lines
        ConfigurableApplicationContext context = SpringApplication.run(FluereApplicationDev.class, args);
        MongoService mongoService = context.getBean(MongoService.class);
        mongoService.performConnectivityCheck();
    }
}
