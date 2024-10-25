package com.example.fluere;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;

@ActiveProfiles("test")
@SpringBootTest(classes = FluereApplicationDev.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FluereApplicationTest {

    static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:4.0.10");


    @BeforeAll
    static void setUp() {
        mongoContainer.start();
        System.setProperty("spring.data.mongodb.uri", mongoContainer.getReplicaSetUrl());
    }

    @Test
    void ContextLoad() {
    }
}