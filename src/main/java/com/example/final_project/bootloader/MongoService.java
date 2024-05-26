package com.example.final_project.bootloader;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class MongoService {
    private final MongoTemplate mongoTemplate;

    //TODO RIGHT NOW LET IT BE BUT LATER REMOVE THIS CLASS FOR DB CONNECTION TEST
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
