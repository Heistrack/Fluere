//package com.example.fluere.expense.repository;
//
//import com.example.fluere.budget.model.BudgetIdWrapper;
//import com.example.fluere.currencyapi.model.MKTCurrency;
//import com.example.fluere.expense.model.Expense;
//import com.example.fluere.expense.model.ExpenseDetails;
//import com.example.fluere.expense.model.ExpenseIdWrapper;
//import com.example.fluere.expense.model.ExpenseType;
//import com.example.fluere.userentity.model.UserIdWrapper;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.MongoDBContainer;
//import org.testcontainers.utility.DockerImageName;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.TreeMap;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@DataMongoTest
//@ActiveProfiles("dev")
//class ExpenseRepositoryTest {
//
//    private static final MongoDBContainer mongoDB = new MongoDBContainer(DockerImageName.parse("mongo:7.0.12"));
//
//    @BeforeAll
//    static void beforeAll() {
//        mongoDB.start();
//    }
//
//    @AfterAll
//    static void tearDown() {
//        mongoDB.stop();
//    }

//    @DynamicPropertySource
//    private void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add();
//    }

//    @Test
//    @Order(1)
//    void should_return_three_expenses_when_find_all() {
//        LocalDateTime now = LocalDateTime.now();
//        ExpenseIdWrapper ex1ExpenseId = ExpenseIdWrapper.newFromString("e4b33e27-2803-4ed1-b026-77def0e45e59");
//        BudgetIdWrapper ex1BudgetId = BudgetIdWrapper.newFromString("6dda569f-24b6-4f30-90c1-53d7af2076e5");
//        UserIdWrapper ex1UserId = UserIdWrapper.newFromString("2161cc56-aad0-4394-b927-f1863b2d1ac9");
//        TreeMap<Integer, LocalDateTime> ex1HistoryOfChanges = new TreeMap<>();
//        ex1HistoryOfChanges.put(1, now);
//        ExpenseDetails ex1ExpenseDetails = ExpenseDetails.newOf("Test title1", BigDecimal.ONE, MKTCurrency.PLN,
//                                                                ex1HistoryOfChanges,
//                                                                ExpenseType.ACCOMMODATION, "Expense's test description."
//        );
//
//        ExpenseIdWrapper ex2ExpenseId = ExpenseIdWrapper.newFromString("9de22688-bf62-4dd7-805d-5b30cab3bf8d");
//        BudgetIdWrapper ex2BudgetId = BudgetIdWrapper.newFromString("ad3da1ab-5f26-40b6-81fd-f520a1dfb32a");
//        UserIdWrapper ex2UserId = UserIdWrapper.newFromString("7db7829e-6c7f-46a5-b840-8786192206b6");
//        TreeMap<Integer, LocalDateTime> ex2HistoryOfChanges = new TreeMap<>();
//        ex2HistoryOfChanges.put(1, now);
//        ExpenseDetails ex2ExpenseDetails = ExpenseDetails.newOf("Test title1", BigDecimal.ONE, MKTCurrency.PLN,
//                                                                ex2HistoryOfChanges,
//                                                                ExpenseType.ACCOMMODATION, "Expense's test description."
//        );
//
////        ExpenseIdWrapper ex3ExpenseId = ExpenseIdWrapper.newFromString("7f55e80e-f327-493f-a461-e7d3efb856c2");
////        BudgetIdWrapper ex3BudgetId = BudgetIdWrapper.newFromString("3dff66be-12eb-4bd3-b868-ddb196bd6cb3");
////        UserIdWrapper ex3UserId = UserIdWrapper.newFromString("56a2b8be-3915-4700-ad47-f048d2fc9667");
////        TreeMap<Integer, LocalDateTime> ex3HistoryOfChanges = new TreeMap<>();
////        ex1HistoryOfChanges.put(1, now);
////        ExpenseDetails ex3ExpenseDetails = ExpenseDetails.newOf("Test title1", BigDecimal.ONE, MKTCurrency.PLN,
////                                                                ex3HistoryOfChanges,
////                                                                ExpenseType.ACCOMMODATION, "Expense's test description."
////        );
//
//        Expense exp1 = Expense.newOf(
//                ex1ExpenseId, ex1BudgetId, ex1UserId, ex1ExpenseDetails);
//        Expense exp2 = Expense.newOf(
//                ex2ExpenseId, ex2BudgetId, ex2UserId, ex2ExpenseDetails);
////        Expense exp3 = Expense.newOf(
////                ex3ExpenseId, ex3BudgetId, ex3UserId, ex3ExpenseDetails);
//
//        underTest.save(exp1);
//        underTest.save(exp2);
////        underTest.save(exp3);
//
//        List<Expense> expenses = underTest.findAll(Expense.class);
//
//        assertEquals(2, expenses.size());
//    }

//}
