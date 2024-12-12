package com.example.fluere.expense.service;

import com.example.fluere.budget.model.BudgetIdWrapper;
import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.expense.model.Expense;
import com.example.fluere.expense.model.ExpenseDetails;
import com.example.fluere.expense.model.ExpenseIdWrapper;
import com.example.fluere.expense.model.ExpenseType;
import com.example.fluere.expense.repository.ExpenseRepository;
import com.example.fluere.expense.service.user.DefaultExpenseService;
import com.example.fluere.expense.service.user.ExpenseInnerServiceLogic;
import com.example.fluere.security.service.jwt.JwtService;
import com.example.fluere.userentity.model.UserIdWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Slf4j
public class DefaultExpenseServiceTest {

    private static final ExpenseIdWrapper EXPENSE_ID = ExpenseIdWrapper.newFromString(
            "85a227c5-d438-4767-8856-a8ff0afb3071");
    private static final BudgetIdWrapper BUDGET_ID = BudgetIdWrapper.newFromString(
            "cf63fc6f-2904-4d25-b806-557ae960b165");
    private static final UserIdWrapper USER_ID =
            UserIdWrapper.newFromString("d58e3dd1-096f-42f9-b975-9c9b2adee788");
    private static final LocalDateTime EXPENSE_DATE =
            LocalDateTime.of(1410, 1, 1, 1, 1);
    private static final ExpenseDetails EXPENSE_DETAILS =
            ExpenseDetails.newOf(
                    "Apple", BigDecimal.ONE, MKTCurrency.PLN,
                    new TreeMap<>(), ExpenseType.ACCOMMODATION, ""
            );
    private static final Expense ENTITY = Expense.newOf(EXPENSE_ID, BUDGET_ID, USER_ID,
                                                        EXPENSE_DETAILS
    );
    private static final Authentication MOCKED_AUTHENTICATION = null;
    private static final Expense basicExpense = Expense.newOf(EXPENSE_ID,
                                                              BUDGET_ID, USER_ID,
                                                              new ExpenseDetails(
                                                                      "Pear",
                                                                      BigDecimal.TEN,
                                                                      MKTCurrency.EUR,
                                                                      EXPENSE_DETAILS.historyOfChanges(),
                                                                      ExpenseType.FOOD,
                                                                      "Test description to change."
                                                              )
    );

    @InjectMocks
    private DefaultExpenseService underTest;
    @Mock
    private ExpenseRepository repository;
    @Mock
    private Supplier<ExpenseIdWrapper> expenseIdSupplier;
    @Mock
    private ExpenseInnerServiceLogic innerServiceLogic;
    @Mock
    private JwtService jwtService;

    @BeforeAll
    static void startUp() {
        ENTITY.expenseDetails().historyOfChanges().put(1, EXPENSE_DATE);
    }

    @Test
    void RegisterNewExpense__When_proper_expense_add__Should_return_expense() {
        Mockito.when(repository.save(Mockito.any())).thenReturn(ENTITY);
        Mockito.when(jwtService.extractUserIdFromRequestAuth(MOCKED_AUTHENTICATION))
               .thenReturn(USER_ID);
        Mockito.when(expenseIdSupplier.get()).thenReturn(EXPENSE_ID);
        LocalDateTime testStartTime = LocalDateTime.now();

        Expense expected = underTest.registerNewExpense(
                BUDGET_ID,
                EXPENSE_DETAILS.title(),
                EXPENSE_DETAILS.amount(),
                EXPENSE_DETAILS.currency(),
                EXPENSE_DETAILS.expenseType(),
                EXPENSE_DETAILS.description(),
                MOCKED_AUTHENTICATION
        );

        assertThat(expected).usingRecursiveComparison()
                            .ignoringFields("expenseDetails.historyOfChanges")
                            .isEqualTo(ENTITY);
        LocalDateTime testTime = expected.expenseDetails().historyOfChanges().get(1);
        assertThat(testTime).isAfterOrEqualTo(testStartTime).isBeforeOrEqualTo(LocalDateTime.now());
        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(innerServiceLogic, Mockito.times(1))
               .validationExpenseAmount(EXPENSE_DETAILS.currency(), EXPENSE_DETAILS.amount(), BUDGET_ID);
        Mockito.verify(innerServiceLogic, Mockito.times(1))
               .addBalance(EXPENSE_DETAILS.currency(), EXPENSE_DETAILS.amount(), BUDGET_ID);
    }

    @Test
    void GetExpenseById__When_expense_no_exists__Should_throw_exception() {
        Mockito.when(jwtService.extractUserIdFromRequestAuth(MOCKED_AUTHENTICATION))
               .thenReturn(USER_ID);
        Mockito.when(repository.findByExpenseIdAndUserId(Mockito.any(), Mockito.any()))
               .thenReturn(Optional.empty());

        NoSuchElementException expected = assertThrows(
                NoSuchElementException.class,
                () -> repository.findByExpenseIdAndUserId(
                        EXPENSE_ID, USER_ID)
        );
        Assertions.assertEquals("Expense doesn't exist.", expected.getMessage());
    }

    @Test
    void UpdateExpenseById__When_proper_expense_update__Should_return_updated_expense() {
        Expense oldExpense = Expense.newOf(EXPENSE_ID,
                                           BUDGET_ID, USER_ID,
                                           new ExpenseDetails(
                                                   "Pear",
                                                   BigDecimal.TEN,
                                                   MKTCurrency.EUR,
                                                   EXPENSE_DETAILS.historyOfChanges(),
                                                   ExpenseType.FOOD,
                                                   "Test description to change."
                                           )
        );
        Mockito.when(jwtService.extractUserIdFromRequestAuth(MOCKED_AUTHENTICATION)).thenReturn(USER_ID);
        Mockito.when(repository.findByExpenseIdAndUserId(Mockito.any(), Mockito.any()))
               .thenReturn(Optional.of(oldExpense));
        Mockito.when(innerServiceLogic.noParamChangeCheck(Mockito.any(), Mockito.any(), Mockito.any(),
                                                          Mockito.any(), Mockito.any(), Mockito.any()
        )).thenReturn(false);
        Mockito.when(repository.save(Mockito.any())).thenReturn(ENTITY);

        Expense expected = underTest.updateExpenseById(
                EXPENSE_ID,
                EXPENSE_DETAILS.title(),
                EXPENSE_DETAILS.amount(),
                EXPENSE_DETAILS.currency(),
                ExpenseType.ACCOMMODATION,
                EXPENSE_DETAILS.description(),
                MOCKED_AUTHENTICATION
        );

        assertThat(expected).isEqualTo(ENTITY);
    }

    @Test
    void UpdateExpenseById__When_no_expense_update__Should_return_same_expense() {
        Expense basicExpense = Expense.newOf(EXPENSE_ID,
                                             BUDGET_ID, USER_ID,
                                             new ExpenseDetails(
                                                     "Pear",
                                                     BigDecimal.TEN,
                                                     MKTCurrency.EUR,
                                                     EXPENSE_DETAILS.historyOfChanges(),
                                                     ExpenseType.FOOD,
                                                     "Test description to change."
                                             )
        );
        Mockito.when(repository.findByExpenseIdAndUserId(Mockito.any(), Mockito.any()))
               .thenReturn(Optional.of(basicExpense));
        Mockito.when(jwtService.extractUserIdFromRequestAuth(MOCKED_AUTHENTICATION))
               .thenReturn(USER_ID);
        Mockito.when(innerServiceLogic.noParamChangeCheck(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
        )).thenReturn(true);


        Expense expected = underTest.updateExpenseById(EXPENSE_ID,
                                                       "Pear", BigDecimal.TEN,
                                                       MKTCurrency.EUR, ExpenseType.FOOD,
                                                       "Test description to change.",
                                                       MOCKED_AUTHENTICATION
        );

        assertThat(expected).isEqualTo(basicExpense);
    }

    @Test
    void UpdateExpenseById__When_no_such_expense__Should_throw_exception() {
        Mockito.when(jwtService.extractUserIdFromRequestAuth(MOCKED_AUTHENTICATION))
               .thenReturn(USER_ID);
        Mockito.when(repository.findByExpenseIdAndUserId(Mockito.any(), Mockito.any()))
               .thenThrow(new NoSuchElementException("Can't update expense, because it doesn't exist"));

        assertThatThrownBy(() -> underTest.updateExpenseById(null,
                                                             null, null,
                                                             null, null,
                                                             null, MOCKED_AUTHENTICATION
        )).hasMessageStartingWith("Can't update expense, because it doesn't exist")
          .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void PatchExpenseContent__When_proper_expense__Should_return_new_expense() {
        Expense newExpense = Expense.newOf(EXPENSE_ID,
                                           BUDGET_ID, USER_ID,
                                           new ExpenseDetails(
                                                   "Changed title",
                                                   BigDecimal.ONE,
                                                   MKTCurrency.PLN,
                                                   EXPENSE_DETAILS.historyOfChanges(),
                                                   ExpenseType.ACCOMMODATION,
                                                   "Changed description"
                                           )
        );
        Mockito.when(jwtService.extractUserIdFromRequestAuth(MOCKED_AUTHENTICATION))
               .thenReturn(USER_ID);
        Mockito.when(repository.findByExpenseIdAndUserId(Mockito.any(), Mockito.any()))
               .thenReturn(Optional.of(basicExpense));


        Expense expected = underTest.patchExpenseContent(
                EXPENSE_ID,
                Optional.of("Changed title"),
                Optional.of(BigDecimal.ONE),
                Optional.of(MKTCurrency.PLN),
                Optional.of(ExpenseType.ACCOMMODATION),
                Optional.of("Changed description"),
                MOCKED_AUTHENTICATION
        );

        assertThat(expected).isEqualTo(newExpense);
        Mockito.verify(jwtService, Mockito.times(1))
               .extractUserIdFromRequestAuth(MOCKED_AUTHENTICATION);
        Mockito.verify(repository, Mockito.times(1))
               .save(newExpense);
        Mockito.verify(repository, Mockito.times(2))
               .findByExpenseIdAndUserId(EXPENSE_ID, USER_ID);
        Mockito.verify(innerServiceLogic, Mockito.times(1))
               .updateHistoryChange(basicExpense);
    }

    @Test
    void PatchExpenseContent__When_no_expense__Should_throw_exception() {
        Mockito.when(jwtService.extractUserIdFromRequestAuth(MOCKED_AUTHENTICATION)).thenReturn(USER_ID);
        Mockito.when(repository.findByExpenseIdAndUserId(EXPENSE_ID, USER_ID))
               .thenReturn(Optional.empty());

        NoSuchElementException expected = assertThrows(NoSuchElementException.class, () ->
                underTest.patchExpenseContent(
                        EXPENSE_ID,
                        Optional.of(EXPENSE_DETAILS.title()),
                        Optional.of(EXPENSE_DETAILS.amount()),
                        Optional.of(EXPENSE_DETAILS.currency()),
                        Optional.of(EXPENSE_DETAILS.expenseType()),
                        Optional.of(EXPENSE_DETAILS.description()),
                        MOCKED_AUTHENTICATION
                ));
        Assertions.assertEquals("There's no such expense.", expected.getMessage());
        Mockito.verify(innerServiceLogic, Mockito.times(0))
               .updateHistoryChange(basicExpense);
    }

    @Test
    void PatchExpenseContent__When_same_expense__Should_return_expense() {
        Mockito.when(jwtService.extractUserIdFromRequestAuth(MOCKED_AUTHENTICATION)).thenReturn(USER_ID);
        Mockito.when(repository.findByExpenseIdAndUserId(Mockito.any(), Mockito.any())).thenReturn(
                Optional.of(basicExpense));
        Mockito.when(innerServiceLogic.noParamChangeCheck(Mockito.any(), Mockito.any(),
                                                          Mockito.any(), Mockito.any(),
                                                          Mockito.any(), Mockito.any()
               ))
               .thenReturn(true);

        Expense expected = underTest.patchExpenseContent(
                EXPENSE_ID,
                Optional.of(EXPENSE_DETAILS.title()),
                Optional.of(EXPENSE_DETAILS.amount()),
                Optional.of(EXPENSE_DETAILS.currency()),
                Optional.of(EXPENSE_DETAILS.expenseType()),
                Optional.of(EXPENSE_DETAILS.description()),
                MOCKED_AUTHENTICATION
        );

        assertThat(expected).isEqualTo(basicExpense);
        Mockito.verify(innerServiceLogic, Mockito.times(0)).updateHistoryChange(basicExpense);
    }

    @Test
    void PatchExpenseContent__When_change_amount_or_currency__Should_validate() {
        Mockito.when(jwtService.extractUserIdFromRequestAuth(MOCKED_AUTHENTICATION)).thenReturn(USER_ID);
        Mockito.when(repository.findByExpenseIdAndUserId(EXPENSE_ID, USER_ID))
               .thenReturn(Optional.of(basicExpense));

        Expense expected = underTest.patchExpenseContent(
                EXPENSE_ID,
                Optional.of(EXPENSE_DETAILS.title()),
                Optional.of(EXPENSE_DETAILS.amount().add(BigDecimal.ONE)),
                Optional.of(MKTCurrency.PLN),
                Optional.of(EXPENSE_DETAILS.expenseType()),
                Optional.of(EXPENSE_DETAILS.description()),
                MOCKED_AUTHENTICATION
        );

        Mockito.verify(innerServiceLogic, Mockito.times(1))
               .validationExpenseAmount(MKTCurrency.PLN, BigDecimal.valueOf(2), BUDGET_ID);
        Mockito.verify(innerServiceLogic, Mockito.times(1))
               .balanceUpdate(MKTCurrency.PLN, BigDecimal.valueOf(2), basicExpense);
    }

    //TODO do pageable methods' tests
}