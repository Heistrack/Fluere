//package com.example.final_project;
//
//import com.example.final_project.api.requests.expenses.appusers.RegisterExpenseRequest;
//import com.example.final_project.api.requests.expenses.appusers.UpdateExpenseRequest;
//import com.example.final_project.api.responses.expenses.appusers.ExpenseResponseDto;
//import com.example.final_project.domain.expenses.ExpenseId;
//import com.example.final_project.expense.service.user.ExpenseService;
//import com.example.final_project.expense.repository.ExpenseRepository;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.*;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import org.testcontainers.containers.MongoDBContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import java.math.BigDecimal;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Testcontainers
//class ExpensesIntegrationTests {
//
//    @Container
//    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.4");
//
//    @Autowired
//    private ExpenseRepository expenseRepository;
//    @Autowired
//    private TestRestTemplate testRestTemplate;
//
//    @Autowired
//    private WebTestClient webClient;
//
//    @Autowired
//    private ExpenseService expensesService;
//
//
//    @BeforeEach
//    void setUp() {
//        expenseRepository.deleteAll();
//    }
//
//    @Test
//    void shouldRegisterNewExpense() {
//        // given
//        var title = "My test expense";
//        var expectedAmount = BigDecimal.valueOf(100);
//        RegisterExpenseRequest request = new RegisterExpenseRequest(title, expectedAmount);
//
//        // when
//        ResponseEntity<ExpenseResponseDto> response = testRestTemplate.postForEntity("/expenses", request, ExpenseResponseDto.class);
//
//        // then
//        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
//        assertThat(response.getBody().title()).isEqualTo(title);
//        assertThat(response.getBody().amount()).isEqualByComparingTo(expectedAmount);
//    }
//
//    void registerNewExpense(String title, BigDecimal expectedAmount) {
//
//        RegisterExpenseRequest request = new RegisterExpenseRequest(title, expectedAmount);
//        // when
//        ResponseEntity<ExpenseResponseDto> response = testRestTemplate.postForEntity("/expenses", request, ExpenseResponseDto.class);
//
//    }
//
//
//    ExpenseResponseDto registerNewExpenseWithReturn(String title, BigDecimal expectedAmount) {
//        RegisterExpenseRequest request = new RegisterExpenseRequest(title, expectedAmount);
//        // when
//        ResponseEntity<ExpenseResponseDto> response = testRestTemplate.postForEntity("/expenses", request, ExpenseResponseDto.class);
//
//        return response.getBody();
//    }
//
//    @AfterEach
//    void tearDown() {
//        expenseRepository.deleteAll();
//    }
//
//    @Test
//    void shouldReturnAllExpenses() {
//
//        // given
//        registerNewExpense("first", BigDecimal.valueOf(1));
//        registerNewExpense("second", BigDecimal.valueOf(2));
//        registerNewExpense("third", BigDecimal.valueOf(3));
//        registerNewExpense("fourth", BigDecimal.valueOf(4));
//        // when
//
//
//        ResponseEntity<List<ExpenseResponseDto>> response = testRestTemplate.exchange("/expenses", HttpMethod.GET, null,
//                new ParameterizedTypeReference<List<ExpenseResponseDto>>() {
//                });
//
//
//        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
//        List<ExpenseResponseDto> expenses = response.getBody();
//        assertThat(expenses).hasSize(4);
//        assertThat(expenses).extracting(ExpenseResponseDto::title).contains("first", "second", "third", "fourth");
//        assertThat(expenses).extracting(ExpenseResponseDto::amount).contains(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4));
//
//    }
//
//    @Test
//    void shouldDeleteSingleExpense() {
////        //given
////        ExpenseId expenseId = new ExpenseId("1");
////        String title = "first";
////        BigDecimal totalMoneySpent = BigDecimal.valueOf(1);
////        Expense expense = new Expense(expenseId, title, totalMoneySpent);
////
////        ResponseEntity<ExpenseResponseDto> response = testRestTemplate.exchange("/expenses{1}", HttpMethod.DELETE, null, )
//
//        // given
//        ExpenseResponseDto response = registerNewExpenseWithReturn("My test expense", BigDecimal.valueOf(100));
//        ExpenseId id = new ExpenseId(response.expenseId());
//        System.out.println(id);
//
//        // when
//        testRestTemplate.delete("/expenses/" + id.id());
//
//        // then
////        assertThat(deleteResponse.getStatusCode().is2xxSuccessful()).isTrue();
//
//        assertThat(expenseRepository.findById(id)).isEmpty();
//
//    }
//    @Test
//    void shouldPutExpense() {
//        //Given
//        ExpenseResponseDto response = registerNewExpenseWithReturn("title", BigDecimal.valueOf(50));
//        ExpenseId id = new ExpenseId(response.expenseId());
//        RegisterExpenseRequest updateRequest = new RegisterExpenseRequest("update Title", BigDecimal.valueOf(100));
//        //When
//        ResponseEntity<ExpenseResponseDto> updateResponse = testRestTemplate.exchange("/expenses/" + id.id(), HttpMethod.PUT, new HttpEntity<>(updateRequest), ExpenseResponseDto.class);
//        //Then
//        assertThat(updateResponse.getStatusCode().is2xxSuccessful()).isTrue();
//        assertThat(updateResponse.getBody().title()).isEqualTo(updateRequest.title());
//        assertThat(updateResponse.getBody().amount()).isEqualTo(updateRequest.amount());
//
//    }
//    @Test
//    void shouldPathExpenses() {
//        //Given
//        ExpenseResponseDto response = registerNewExpenseWithReturn("title", BigDecimal.valueOf(20));
//        ExpenseId id = new ExpenseId(response.expenseId());
//        UpdateExpenseRequest updateRequest = new UpdateExpenseRequest("update Title", null);
//        ExpenseResponseDto expectedResponseDto = new ExpenseResponseDto("update Title", response.expenseId(), BigDecimal.valueOf(20));
//        //When
//        var responseSpec = webClient.patch().uri("/expenses/" + id.id()).bodyValue(updateRequest).exchange();
//        //Then
//        responseSpec.expectStatus().is2xxSuccessful();
//        responseSpec.expectBody(ExpenseResponseDto.class).isEqualTo(expectedResponseDto);
//
//    }
//}