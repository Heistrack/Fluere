package com.example.fluere;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = FluereApplicationDev.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FluereApplicationTest {

    @Test
    void ContextLoad() {
    }
}