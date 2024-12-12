package com.example.fluere.expense.controller;

import com.example.fluere.FluereApplicationTest;
import com.example.fluere.budget.model.BudgetIdWrapper;
import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.expense.controller.user.ExpenseController;
import com.example.fluere.expense.model.Expense;
import com.example.fluere.expense.model.ExpenseDetails;
import com.example.fluere.expense.model.ExpenseIdWrapper;
import com.example.fluere.expense.model.ExpenseType;
import com.example.fluere.expense.service.user.DefaultExpenseService;
import com.example.fluere.userentity.model.UserIdWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

@ActiveProfiles("test")
@WebMvcTest(ExpenseController.class)
@ContextConfiguration(classes = FluereApplicationTest.class)
@WithMockUser(username = "admin", password = "colombo")
public class ExpenseUserControllerTest {
    private static final ExpenseIdWrapper EXPENSE_ID = ExpenseIdWrapper.newFromString(
            "85a227c5-d438-4767-8856-a8ff0afb3071");
    private static final BudgetIdWrapper BUDGET_ID = BudgetIdWrapper.newFromString(
            "cf63fc6f-2904-4d25-b806-557ae960b165");
    private static final UserIdWrapper USER_ID = UserIdWrapper.newFromString("d58e3dd1-096f-42f9-b975-9c9b2adee788");
    private static final String BASE_EXPENSE_CONTROLLER_PATH = "/api/expenses";
    private static final LocalDateTime EXPENSE_DATE = LocalDateTime.of(1, 1, 1, 1, 1);
    private static final ExpenseDetails EXPENSE_DETAILS = ExpenseDetails.newOf(
            "Apple", BigDecimal.ONE, MKTCurrency.PLN, new TreeMap<>(), ExpenseType.ACCOMMODATION, "");
    private static final Expense ENTITY = Expense.newOf(EXPENSE_ID, BUDGET_ID, USER_ID, EXPENSE_DETAILS);
    @Autowired
    private MockMvc mockMvc;
//    @Autowired
//    private ExpenseController controller;
    @MockBean
    private DefaultExpenseService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void someTest() {

    }
}

//    @MockBean
//    private JwtService jwtService;
//    @MockBean
//    private UserDetailsService userDetailsService;
//    @MockBean
//    private MongoTemplate mongoTemplate;
//
//    @Container
//    static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:4.4.2");
//
//    @DynamicPropertySource
//    static void mongoProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
//    }
//
//    @BeforeAll
//    static void beforeAll() {
//        mongoContainer.start();
//        ENTITY.expenseDetails().historyOfChanges().put(1, EXPENSE_DATE);
//    }

//    @Test
//    void ContextLoad() {
//        assertThat(controller).isNotNull();
//    }

//    @Test
//    void Post__When_proper_expense_add__Should_return_expense() throws Exception {
//        Mockito.when(service.registerNewExpense(
//                ENTITY.budgetId(),
//                EXPENSE_DETAILS.title(),
//                EXPENSE_DETAILS.amount(),
//                EXPENSE_DETAILS.currency(),
//                EXPENSE_DETAILS.expenseType(),
//                EXPENSE_DETAILS.description(),
//                null
//        )).thenReturn(ENTITY);
//
//        String entityJSON = objectMapper.writeValueAsString(
//                new RegisterExpenseRequest(
//                        ENTITY.budgetId().id().toString(), EXPENSE_DETAILS.title(),
//                        EXPENSE_DETAILS.amount(),
//                        EXPENSE_DETAILS.currency(),
//                        EXPENSE_DETAILS.expenseType(),
//                        EXPENSE_DETAILS.description()
//                ));
//
//        mockMvc.perform(MockMvcRequestBuilders.post(BASE_EXPENSE_CONTROLLER_PATH)
//                                              .with(csrf().asHeader())
//                                              .content(entityJSON))
//               .andExpect(MockMvcResultMatchers.status().isCreated())
//               .andExpect(MockMvcResultMatchers.jsonPath("$.title", equalTo("Apple")))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.expenseId", equalTo(EXPENSE_ID)))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.budgetId", equalTo(BUDGET_ID)))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.amount", equalTo(EXPENSE_DETAILS.amount())))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.currency", equalTo(EXPENSE_DETAILS.currency())))
//               .andExpect(MockMvcResultMatchers.jsonPath(
//                       "$.historyOfChanges",
//                       equalTo(EXPENSE_DETAILS.historyOfChanges())
//               ))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.expenseType", equalTo(EXPENSE_DETAILS.expenseType())))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.description", equalTo(EXPENSE_DETAILS.description())));
//    }
//    //TODO check why it doesn't work, main class doesn't seem to see app context.
//}
