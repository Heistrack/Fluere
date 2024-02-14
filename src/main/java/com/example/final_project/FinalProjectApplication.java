package com.example.final_project;

import com.example.final_project.domain.securities.jwtauth.MongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@SpringBootApplication
@EnableMongoRepositories
@Slf4j
public class FinalProjectApplication {

    public static void main(String[] args) {
//		SpringApplication.run(FinalProjectApplication.class, args);
		//TODO remove this connectivity check lines
        ConfigurableApplicationContext context = SpringApplication.run(FinalProjectApplication.class, args);
        MongoService mongoService = context.getBean(MongoService.class);
        mongoService.performConnectivityCheck();
    }

}
