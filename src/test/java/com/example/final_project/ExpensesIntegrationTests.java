package com.example.final_project;

import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseId;
import com.example.final_project.domain.expenses.ExpensesService;
import com.example.final_project.infrastructure.exprepo.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

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
    private WebTestClient webClient;

    @Autowired
    private ExpensesService expensesService;

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
    }

    @Test
    void checkIfTestContainersRun() {
        expenseRepository.save(new Expense(new ExpenseId("123"), "Test expense", BigDecimal.valueOf(200), BudgetId.newOf("321")));

        System.out.println(expenseRepository.findExpenseByBudgetId(BudgetId.newOf("321")));
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        System.out.println(mongoDBContainer.getConnectionString());
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getConnectionString() + "/budgets");
    }
}