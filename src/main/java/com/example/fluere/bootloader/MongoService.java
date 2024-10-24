package com.example.fluere.bootloader;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.property.service.bootloader-info", havingValue = "true")
public class MongoService {
    private final MongoTemplate mongoTemplate;

    public void performConnectivityCheck() {
        try {
            mongoTemplate.getCollectionNames().forEach(System.out::println);
            System.out.println("MongoDB connectivity check successful.");
        } catch (Exception e) {
            System.err.println("MongoDB connectivity check failed: " + e.getMessage());
        }
    }
}
