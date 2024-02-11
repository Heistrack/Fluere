package com.example.final_project.domain.securities.jwtauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class MongoService {
    private final MongoTemplate mongoTemplate;
//TODO RIGHT NOW LET IT BE BUT LATER REMOVE THIS CLASS FOR DB CONNECTION TEST
    @Autowired
    public MongoService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void performConnectivityCheck() {
        try {
            // Try a simple operation, e.g., listing collections
            mongoTemplate.getCollectionNames().forEach(System.out::println);
            System.out.println("MongoDB connectivity check successful.");
        } catch (Exception e) {
            System.err.println("MongoDB connectivity check failed: " + e.getMessage());
        }
    }
}
