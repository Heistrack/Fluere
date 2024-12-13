package com.example.fluere.expense.service;

import com.example.fluere.budget.model.Budget;
import com.example.fluere.budget.model.BudgetIdWrapper;
import com.example.fluere.budget.repository.BudgetRepository;
import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.expense.model.Expense;
import com.example.fluere.expense.model.ExpenseDetails;
import com.example.fluere.expense.model.ExpenseIdWrapper;
import com.example.fluere.expense.model.ExpenseType;
import com.example.fluere.expense.repository.ExpenseRepository;
import com.example.fluere.expense.service.admin.AdminDefaultExpenseService;
import com.example.fluere.expense.service.user.ExpenseInnerServiceLogic;
import com.example.fluere.userentity.model.UserIdWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AdminDefaultExpenseServiceTest {

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

    @InjectMocks
    private AdminDefaultExpenseService underTest;
    @Mock
    private ExpenseInnerServiceLogic innerServiceLogic;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private Supplier<ExpenseIdWrapper> expenseIdSupplier;

    @BeforeAll
    static void startUp() {
        ENTITY.expenseDetails().historyOfChanges().put(1, EXPENSE_DATE);
    }

    @Test
    void RegisterNewExpense__When_proper_expense_add__Should_return_expense() {
        Mockito.when(budgetRepository.findById(BUDGET_ID))
               .thenReturn(Optional.of(Budget.newOf(BUDGET_ID, USER_ID, null)));
        Mockito.when(expenseIdSupplier.get()).thenReturn(EXPENSE_ID);
        LocalDateTime testStartTime = LocalDateTime.now();

        Expense expected = underTest.registerNewExpense(BUDGET_ID, EXPENSE_DETAILS.title(), EXPENSE_DETAILS.amount(),
                                                        EXPENSE_DETAILS.currency(), EXPENSE_DETAILS.expenseType(),
                                                        EXPENSE_DETAILS.description()
        );

        assertThat(expected).usingRecursiveComparison()
                            .ignoringFields("expenseDetails.historyOfChanges")
                            .isEqualTo(ENTITY);

        LocalDateTime testTime = expected.expenseDetails().historyOfChanges().get(1);
        assertThat(testTime).isAfterOrEqualTo(testStartTime).isBeforeOrEqualTo(LocalDateTime.now());
        Mockito.verify(innerServiceLogic, Mockito.times(1))
               .validationExpenseAmount(EXPENSE_DETAILS.currency(), EXPENSE_DETAILS.amount(), BUDGET_ID);
        Mockito.verify(innerServiceLogic, Mockito.times(1))
               .addBalance(EXPENSE_DETAILS.currency(), EXPENSE_DETAILS.amount(), BUDGET_ID);
        Mockito.verify(expenseRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void RegisterNewExpense__When_improper_expense_add__Should_throw_exception() {
        Mockito.when(budgetRepository.findById(BUDGET_ID)).thenReturn(Optional.empty());

        NoSuchElementException expected = assertThrows(
                NoSuchElementException.class, () -> underTest.registerNewExpense(
                        BUDGET_ID,
                        EXPENSE_DETAILS.title(),
                        EXPENSE_DETAILS.amount(),
                        EXPENSE_DETAILS.currency(),
                        EXPENSE_DETAILS.expenseType(),
                        EXPENSE_DETAILS.description()
                ));
        Assertions.assertEquals(expected.getMessage(), "No user found.");
    }

    @Test
    void GetExpenseById__When_no_expense__Should_throw_exception() {
        Mockito.when(expenseRepository.findById(EXPENSE_ID)).thenReturn(Optional.empty());

        NoSuchElementException expected = assertThrows(
                NoSuchElementException.class, () -> underTest.getExpenseById(EXPENSE_ID));

        Assertions.assertEquals(expected.getMessage(), "Expense doesn't exist.");
    }

    @Test
    void GetAllExpensesByBudgetId__When_proper_budget_get__Should_return_expenses() {
        Mockito.when(expenseRepository.findAllByBudgetId(Mockito.any(), Mockito.any())).thenReturn(Page.empty());

        underTest.getAllExpensesByBudgetId(BUDGET_ID, Page.empty().getPageable());

        Mockito.verify(expenseRepository, Mockito.times(1)).findAllByBudgetId(BUDGET_ID, Page.empty().getPageable());
    }

    @Test
    void GetAllExpensesByUserId__When_proper_userId__Should_return_expenses() {
        Mockito.when(expenseRepository.findAllByUserId(Mockito.any(), Mockito.any())).thenReturn(Page.empty());

        underTest.getAllExpensesByUserId(USER_ID, Page.empty().getPageable());

        Mockito.verify(expenseRepository, Mockito.times(1)).findAllByUserId(USER_ID, Page.empty().getPageable());
    }

    @Test
    void GetAllExpensesByPage_When_proper_page__Should_return_expenses() {
        Mockito.when(expenseRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Page.empty());

        underTest.getAllExpensesByPage(Page.empty().getPageable());

        Mockito.verify(expenseRepository, Mockito.times(1)).findAll(Page.empty().getPageable());
    }

    @Test
    void UpdateExpenseById__When_proper_expense__Should_return_updated_expense() {
        Mockito.when(expenseRepository.findById(EXPENSE_ID)).thenReturn(Optional.of(ENTITY));

        String changeTitle = "Changed title";
        BigDecimal changedAmount = BigDecimal.TEN;
        MKTCurrency changedCurrency = MKTCurrency.PLN;
        ExpenseType changedExpenseType = ExpenseType.CLOTHES;
        String changedDescription = "Changed description";
        ExpenseDetails updatedExpenseDetails = ExpenseDetails.newOf(changeTitle, changedAmount, changedCurrency,
                                                                    EXPENSE_DETAILS.historyOfChanges(),
                                                                    changedExpenseType, changedDescription
        );
        Expense updatedExpense = Expense.newOf(EXPENSE_ID, BUDGET_ID, USER_ID, updatedExpenseDetails);


        Expense expected = underTest.updateExpenseById(
                EXPENSE_ID, changeTitle, changedAmount, changedCurrency, changedExpenseType, changedDescription);

        assertThat(updatedExpense).usingRecursiveComparison()
                                  .ignoringFields("expenseDetails.historyOfChanges")
                                  .isEqualTo(expected);

        Mockito.verify(expenseRepository, Mockito.times(1)).findById(EXPENSE_ID);
        Mockito.verify(innerServiceLogic, Mockito.times(1)).noParamChangeCheck(Mockito.any(), Mockito.any(),
                                                                               Mockito.any(), Mockito.any(),
                                                                               Mockito.any(), Mockito.any()
        );
        Mockito.verify(innerServiceLogic, Mockito.times(1))
               .validationExpenseAmount(changedCurrency, changedAmount, BUDGET_ID);
        Mockito.verify(innerServiceLogic, Mockito.times(1)).balanceUpdate(changedCurrency, changedAmount, ENTITY);
        Mockito.verify(innerServiceLogic, Mockito.times(1)).updateHistoryChange(Mockito.any());
        Mockito.verify(expenseRepository, Mockito.times(1)).save(expected);
    }

    @Test
    void UpdateExpenseById__When_no_expense__Should_throw_exception() {
        Mockito.when(expenseRepository.findById(EXPENSE_ID)).thenReturn(Optional.empty());

        NoSuchElementException expected = assertThrows(
                NoSuchElementException.class, () -> underTest.updateExpenseById(EXPENSE_ID, EXPENSE_DETAILS.title(),
                                                                                EXPENSE_DETAILS.amount(),
                                                                                EXPENSE_DETAILS.currency(),
                                                                                EXPENSE_DETAILS.expenseType(),
                                                                                EXPENSE_DETAILS.description()
                ));

        Assertions.assertEquals(expected.getMessage(), "Expense doesn't exist.");
    }

    @Test
    void UpdateExpenseById__When_same_expense__Should_return_expense() {
        Mockito.when(expenseRepository.findById(EXPENSE_ID)).thenReturn(Optional.of(ENTITY));
        Mockito.when(innerServiceLogic.noParamChangeCheck(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                                                          Mockito.any(), Mockito.any()
        )).thenReturn(true);

        Expense expected = underTest.updateExpenseById(EXPENSE_ID, EXPENSE_DETAILS.title(),
                                                       EXPENSE_DETAILS.amount(),
                                                       EXPENSE_DETAILS.currency(),
                                                       EXPENSE_DETAILS.expenseType(),
                                                       EXPENSE_DETAILS.description()
        );

        assertThat(expected).isEqualTo(ENTITY);
        Mockito.verify(innerServiceLogic, Mockito.times(0)).updateHistoryChange(Mockito.any());
    }

    @Test
    void PatchExpenseContent__When_proper_expense__Should_return_patched_expense() {
        Mockito.when(expenseRepository.findById(EXPENSE_ID)).thenReturn(Optional.of(ENTITY));

        String changeTitle = "Changed title";
        BigDecimal changedAmount = BigDecimal.TEN;
        MKTCurrency changedCurrency = MKTCurrency.PLN;
        ExpenseType changedExpenseType = ExpenseType.CLOTHES;
        String changedDescription = "Changed description";
        ExpenseDetails updatedExpenseDetails = ExpenseDetails.newOf(changeTitle, changedAmount, changedCurrency,
                                                                    EXPENSE_DETAILS.historyOfChanges(),
                                                                    changedExpenseType, changedDescription
        );

        Expense patchedExpense = Expense.newOf(EXPENSE_ID, BUDGET_ID, USER_ID, updatedExpenseDetails);
        Expense expected = underTest.patchExpenseContent(
                EXPENSE_ID,
                Optional.of(changeTitle),
                Optional.of(changedAmount),
                Optional.of(changedCurrency),
                Optional.of(changedExpenseType),
                Optional.of(changedDescription)
        );

        assertThat(expected).usingRecursiveComparison()
                            .ignoringFields("expenseDetails.historyOfChanges")
                            .isEqualTo(patchedExpense);

        Mockito.verify(innerServiceLogic, Mockito.times(1)).noParamChangeCheck(
                ENTITY,
                Optional.of(changeTitle),
                Optional.of(changedAmount),
                Optional.of(changedCurrency),
                Optional.of(changedExpenseType),
                Optional.of(changedDescription)
        );
        Mockito.verify(innerServiceLogic, Mockito.times(1))
               .validationExpenseAmount(changedCurrency, changedAmount, BUDGET_ID);
        Mockito.verify(innerServiceLogic, Mockito.times(1))
               .balanceUpdate(changedCurrency, changedAmount, ENTITY);
        Mockito.verify(innerServiceLogic, Mockito.times(1))
               .updateHistoryChange(ENTITY);
        Mockito.verify(expenseRepository, Mockito.times(1)).save(patchedExpense);
    }

    @Test
    void PatchExpenseContent__When_no_expense__Should_throw_exception() {
        Mockito.when(expenseRepository.findById(EXPENSE_ID)).thenReturn(Optional.empty());

        NoSuchElementException expected = assertThrows(
                NoSuchElementException.class,
                () -> underTest.patchExpenseContent(
                        EXPENSE_ID,
                        Optional.of(EXPENSE_DETAILS.title()),
                        Optional.of(EXPENSE_DETAILS.amount()),
                        Optional.of(EXPENSE_DETAILS.currency()),
                        Optional.of(EXPENSE_DETAILS.expenseType()),
                        Optional.of(EXPENSE_DETAILS.description())
                )
        );

        assertThat(expected.getMessage()).isEqualTo("There's no such expense.");
    }

    @Test
    void PatchExpenseContent__When_same_expense__Should_return_expense() {
        Mockito.when(expenseRepository.findById(EXPENSE_ID)).thenReturn(Optional.of(ENTITY));
        Optional<String> sameTitle = Optional.of(EXPENSE_DETAILS.title());
        Optional<BigDecimal> sameAmount = Optional.of(EXPENSE_DETAILS.amount());
        Optional<MKTCurrency> sameCurrency = Optional.of(EXPENSE_DETAILS.currency());
        Optional<ExpenseType> sameNewExpenseType = Optional.of(EXPENSE_DETAILS.expenseType());
        Optional<String> sameDescription = Optional.of(EXPENSE_DETAILS.description());
        Mockito.when(innerServiceLogic.noParamChangeCheck(
                ENTITY,
                sameTitle,
                sameAmount,
                sameCurrency,
                sameNewExpenseType,
                sameDescription
        )).thenReturn(true);

        Expense expected = underTest.patchExpenseContent(
                EXPENSE_ID,
                sameTitle,
                sameAmount,
                sameCurrency,
                sameNewExpenseType,
                sameDescription
        );

        Assertions.assertEquals(expected, ENTITY);
        Mockito.verify(innerServiceLogic, Mockito.times(0)).updateHistoryChange(Mockito.any());
    }

    @Test
    void PatchExpenseContent__When_change_amount_or_currency__Should_validate() {
        Mockito.when(expenseRepository.findById(EXPENSE_ID))
               .thenReturn(Optional.of(ENTITY));

        Expense expected = underTest.patchExpenseContent(
                EXPENSE_ID,
                Optional.of(EXPENSE_DETAILS.title()),
                Optional.of(EXPENSE_DETAILS.amount().add(BigDecimal.ONE)),
                Optional.of(MKTCurrency.PLN),
                Optional.of(EXPENSE_DETAILS.expenseType()),
                Optional.of(EXPENSE_DETAILS.description())
        );

        Mockito.verify(innerServiceLogic, Mockito.times(1))
               .validationExpenseAmount(MKTCurrency.PLN, BigDecimal.valueOf(2), BUDGET_ID);
        Mockito.verify(innerServiceLogic, Mockito.times(1))
               .balanceUpdate(MKTCurrency.PLN, BigDecimal.valueOf(2), ENTITY);
    }

    @Test
    void DeleteExpenseById__When_method_invoked__Should_invoke_repository() {

        underTest.deleteExpenseById(EXPENSE_ID);

        Mockito.verify(expenseRepository, Mockito.times(1)).deleteById(EXPENSE_ID);
    }

    //TODO make pageable tests later
}
