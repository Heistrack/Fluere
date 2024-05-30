package com.example.fluere;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest(classes = FluereApplicationDev.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FluereApplicationTest {
        //TODO add additional mongo container for test db and set restart always via using docker compose in docker desktop
    //TODO one commit daily today I want to establish separate docker container
//TODO add init file for mongo db for test and development one.
    @Test
    void ContextLoad() {
    }
}