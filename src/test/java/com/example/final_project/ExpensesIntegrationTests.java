package com.example.final_project;


import com.example.final_project.api.ExpensesController;
import com.example.final_project.api.requests.expenses.RegisterExpenseRequest;
import com.example.final_project.api.responses.ExpenseResponseDto;
import com.example.final_project.domain.expenses.ExpensesService;
import com.example.final_project.infrastructure.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ExpensesIntegrationTests {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.4");

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ExpensesService expensesService;


    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
    }

    @Test
    void shouldRegisterNewExpense() {
        // given
        var title = "My test expense";
        var expectedAmount = BigDecimal.valueOf(100);
        RegisterExpenseRequest request = new RegisterExpenseRequest(title, expectedAmount);

        // when
        ResponseEntity<ExpenseResponseDto> response = testRestTemplate.postForEntity("/expenses", request, ExpenseResponseDto.class);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().title()).isEqualTo(title);
        assertThat(response.getBody().amount()).isEqualByComparingTo(expectedAmount);
    }


}