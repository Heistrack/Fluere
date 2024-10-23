package com.example.fluere.expense.controller;

import com.example.fluere.budget.model.BudgetIdWrapper;
import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.expense.controller.user.ExpenseController;
import com.example.fluere.expense.model.Expense;
import com.example.fluere.expense.model.ExpenseDetails;
import com.example.fluere.expense.model.ExpenseIdWrapper;
import com.example.fluere.expense.model.ExpenseType;
import com.example.fluere.expense.request.appuser.RegisterExpenseRequest;
import com.example.fluere.expense.service.user.DefaultExpenseService;
import com.example.fluere.security.util.JwtAuthFilter;
import com.example.fluere.userentity.model.UserIdWrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@ActiveProfiles("test")
@WebMvcTest(controllers = ExpenseController.class)
//@ContextConfiguration(classes = FluereApplicationDev.class)
//@WithMockUser(username = "admin", password = "colombo")
class ExpenseUserControllerTest {
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
    @Autowired
    private ExpenseController controller;
    @MockBean
    private DefaultExpenseService service;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private MongoTemplate mongoTemplate;


    @BeforeAll
    static void beforeAll() {
        ENTITY.expenseDetails().historyOfChanges().put(1, EXPENSE_DATE);
    }

    @Test
    void ContextLoad() {
        assertThat(controller).isNotNull();
    }

    @Test
    void Post__When_proper_expense_add__Should_return_expense() throws Exception {
        Mockito.when(service.registerNewExpense(
                ENTITY.budgetId(),
                EXPENSE_DETAILS.title(),
                EXPENSE_DETAILS.amount(),
                EXPENSE_DETAILS.currency(),
                EXPENSE_DETAILS.expenseType(),
                EXPENSE_DETAILS.description(),
                null
        )).thenReturn(ENTITY);

        String entityJSON = objectMapper.writeValueAsString(
                new RegisterExpenseRequest(
                        ENTITY.budgetId().id().toString(), EXPENSE_DETAILS.title(),
                        EXPENSE_DETAILS.amount(),
                        EXPENSE_DETAILS.currency(),
                        EXPENSE_DETAILS.expenseType(),
                        EXPENSE_DETAILS.description()
                ));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_EXPENSE_CONTROLLER_PATH)
                                              .with(csrf().asHeader())
                                              .content(entityJSON))
               .andExpect(MockMvcResultMatchers.status().isCreated())
               .andExpect(MockMvcResultMatchers.jsonPath("$.title", equalTo("Apple")))
               .andExpect(MockMvcResultMatchers.jsonPath("$.expenseId", equalTo(EXPENSE_ID)))
               .andExpect(MockMvcResultMatchers.jsonPath("$.budgetId", equalTo(BUDGET_ID)))
               .andExpect(MockMvcResultMatchers.jsonPath("$.amount", equalTo(EXPENSE_DETAILS.amount())))
               .andExpect(MockMvcResultMatchers.jsonPath("$.currency", equalTo(EXPENSE_DETAILS.currency())))
               .andExpect(MockMvcResultMatchers.jsonPath(
                       "$.historyOfChanges",
                       equalTo(EXPENSE_DETAILS.historyOfChanges())
               ))
               .andExpect(MockMvcResultMatchers.jsonPath("$.expenseType", equalTo(EXPENSE_DETAILS.expenseType())))
               .andExpect(MockMvcResultMatchers.jsonPath("$.description", equalTo(EXPENSE_DETAILS.description())));
    }
    //TODO check why it doesn't work, main class doesn't seem to see app context.
}
