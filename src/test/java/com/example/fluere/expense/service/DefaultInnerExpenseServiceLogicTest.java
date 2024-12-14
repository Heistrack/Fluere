package com.example.fluere.expense.service;

import com.example.fluere.budget.model.*;
import com.example.fluere.budget.repository.BudgetRepository;
import com.example.fluere.currencyapi.model.FiatCurrencyDailyData;
import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.currencyapi.repository.CurrencyRepository;
import com.example.fluere.exception.custom.ExpenseTooBigException;
import com.example.fluere.expense.model.Expense;
import com.example.fluere.expense.model.ExpenseDetails;
import com.example.fluere.expense.model.ExpenseIdWrapper;
import com.example.fluere.expense.model.ExpenseType;
import com.example.fluere.expense.service.user.DefaultInnerExpenseServiceLogic;
import com.example.fluere.userentity.model.UserIdWrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class DefaultInnerExpenseServiceLogicTest {

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
                    "Apple", BigDecimal.ONE, MKTCurrency.EUR,
                    new TreeMap<>(), ExpenseType.FOOD, ""
            );
    private static final String BUDGET_TITLE = "Food";
    private static final BigDecimal BUDGET_LIMIT = BigDecimal.TEN;
    private static final BudgetType BUDGET_TYPE = BudgetType.HALF;
    private static final String DESCRIPTION = "Budget description";
    private static final BigDecimal MAX_SINGLE_EXPENSE = BigDecimal.valueOf(2);
    private static final MKTCurrency DEFAULT_CURRENCY = MKTCurrency.EUR;
    private static final ExpenseSet EXPENSE_SET = ExpenseSet.newOf(DEFAULT_CURRENCY);
    private static final BudgetPeriod BUDGET_PERIOD = BudgetPeriod.newOf(
            LocalDate.of(1, 1, 1),
            LocalDate.of(2000, 12, 12)
    );
    private static final BudgetDetails BUDGET_DETAILS = BudgetDetails.newOf(BUDGET_TITLE, BUDGET_LIMIT, BUDGET_TYPE,
                                                                            MAX_SINGLE_EXPENSE, DEFAULT_CURRENCY,
                                                                            EXPENSE_SET, new TreeMap<>(), BUDGET_PERIOD,
                                                                            DESCRIPTION
    );
    private static final Budget BUDGET = Budget.newOf(BUDGET_ID, USER_ID, BUDGET_DETAILS);
    private static final Expense EXPENSE = Expense.newOf(EXPENSE_ID, BUDGET_ID, USER_ID,
                                                         EXPENSE_DETAILS
    );
    private static final HashMap<MKTCurrency, BigDecimal> CONVERSION_RATES = new HashMap<>();
    @InjectMocks
    private DefaultInnerExpenseServiceLogic underTest;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private CurrencyRepository currencyRepository;


    @BeforeAll
    static void startUp() {
        EXPENSE.expenseDetails().historyOfChanges().put(1, EXPENSE_DATE);
        BUDGET.budgetDetails().historyOfChanges().put(1, LocalDateTime.now());
        CONVERSION_RATES.put(MKTCurrency.EUR, BigDecimal.TWO);
        CONVERSION_RATES.put(MKTCurrency.PLN, BigDecimal.TEN);
    }

    @Test
    void BalanceUpdate__When_proper_budget__Should_save() {
        Mockito.when(budgetRepository.findById(BUDGET_ID)).thenReturn(Optional.of(BUDGET));

        underTest.balanceUpdate(EXPENSE_DETAILS.currency(), EXPENSE_DETAILS.amount(), EXPENSE);

        Mockito.verify(budgetRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void BalanceUpdate__When_improper_budget__Should_throw() {
        Mockito.when(budgetRepository.findById(BUDGET_ID)).thenReturn(Optional.empty());

        NoSuchElementException result = assertThrows(
                NoSuchElementException.class,
                () -> underTest.balanceUpdate(EXPENSE_DETAILS.currency(), EXPENSE_DETAILS.amount(),
                                              EXPENSE
                )
        );

        assertEquals(result.getMessage(), "Budget not found.");
    }

    @Test
    void AddBalance__When_proper_budget__Should_save() {
        Mockito.when(budgetRepository.findById(BUDGET_ID)).thenReturn(Optional.of(BUDGET));

        underTest.addBalance(EXPENSE_DETAILS.currency(), EXPENSE_DETAILS.amount(), BUDGET_ID);

        Mockito.verify(budgetRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void AddBalance__When_improper_budget__Should_throw() {
        Mockito.when(budgetRepository.findById(BUDGET_ID)).thenReturn(Optional.empty());

        NoSuchElementException result = assertThrows(
                NoSuchElementException.class,
                () -> underTest.addBalance(EXPENSE_DETAILS.currency(), EXPENSE_DETAILS.amount(),
                                           BUDGET_ID
                )
        );

        assertEquals(result.getMessage(), "Budget not found.");
    }

    @Test
    void UpdateHistoryChange__When_proper_expense__Should_put_new_record() {
        LocalDateTime start = LocalDateTime.now();

        underTest.updateHistoryChange(EXPENSE);


        assertThat(EXPENSE.expenseDetails().historyOfChanges().get(2)).isAfterOrEqualTo(start)
                                                                      .isBeforeOrEqualTo(LocalDateTime.now());
        EXPENSE_DETAILS.historyOfChanges().remove(2);
    }

    @Test
    void NoParamChangeCheck__When_same_params__Should_return_true() {
        boolean result = underTest.noParamChangeCheck(EXPENSE, Optional.of(EXPENSE.expenseDetails().title()),
                                                      Optional.of(EXPENSE.expenseDetails().amount()),
                                                      Optional.of(EXPENSE.expenseDetails().currency()),
                                                      Optional.of(EXPENSE.expenseDetails().expenseType()),
                                                      Optional.of(EXPENSE.expenseDetails().description())
        );

        assertTrue(result);
    }

    @Test
    void NoParamChangeCheck__When_no_params__Should_return_true() {
        boolean result = underTest.noParamChangeCheck(EXPENSE, Optional.empty(), Optional.empty(), Optional.empty(),
                                                      Optional.empty(),
                                                      Optional.empty()
        );

        assertTrue(result);
    }

    @Test
    void NoParamChangeCheck__When_new_title__Should_return_false() {
        boolean result = underTest.noParamChangeCheck(EXPENSE, Optional.of("Changed title"),
                                                      Optional.empty(),
                                                      Optional.empty(),
                                                      Optional.empty(),
                                                      Optional.empty()
        );

        assertFalse(result);
    }

    @Test
    void NoParamChangeCheck__When_new_amount__Should_return_false() {
        boolean result = underTest.noParamChangeCheck(EXPENSE, Optional.empty(),
                                                      Optional.of(BigDecimal.TWO),
                                                      Optional.empty(),
                                                      Optional.empty(),
                                                      Optional.empty()
        );

        assertFalse(result);
    }

    @Test
    void NoParamChangeCheck__When_new_currency__Should_return_false() {
        boolean result = underTest.noParamChangeCheck(EXPENSE, Optional.empty(),
                                                      Optional.empty(),
                                                      Optional.of(MKTCurrency.PLN),
                                                      Optional.empty(),
                                                      Optional.empty()
        );

        assertFalse(result);
    }

    @Test
    void NoParamChangeCheck__When_new_expense_type__Should_return_false() {
        boolean result = underTest.noParamChangeCheck(EXPENSE, Optional.empty(),
                                                      Optional.empty(),
                                                      Optional.empty(),
                                                      Optional.of(ExpenseType.ACCOMMODATION),
                                                      Optional.empty()
        );

        assertFalse(result);
    }

    @Test
    void NoParamChangeCheck__When_new_description__Should_return_false() {
        boolean result = underTest.noParamChangeCheck(EXPENSE, Optional.empty(),
                                                      Optional.empty(),
                                                      Optional.empty(),
                                                      Optional.empty(),
                                                      Optional.of("Changed description")
        );

        assertFalse(result);
    }

    @Test
    void SingleMaxExpValidation__When_exceeded_amount__Should_throw() {

        ExpenseTooBigException result = assertThrows(
                ExpenseTooBigException.class, () -> underTest.singleMaxExpValidation(
                        EXPENSE_DETAILS.currency(), BigDecimal.valueOf(3), BUDGET));

        assertEquals(result.getMessage(), "Expense exceed single maximal expense amount in the budget!");
    }

    @Test
    void CheckBudgetLimit__When_amount_too_big__Should_throw() {

        ExpenseTooBigException result = assertThrows(
                ExpenseTooBigException.class,
                () -> underTest.checkBudgetLimit(EXPENSE_DETAILS.currency(), BigDecimal.valueOf(16), BUDGET)
        );

        assertEquals(result.getMessage(), "Expense exceed the budget limit!");
    }

    @Test
    void CheckBudgetLimit__When_expense_type_full__Should_return() {
        DefaultInnerExpenseServiceLogic underTestSpied = Mockito.spy(
                new DefaultInnerExpenseServiceLogic(budgetRepository, currencyRepository));
        Budget test = Budget.newOf(null, null, BudgetDetails.builder().budgetType(BudgetType.FULL).build());

        underTestSpied.checkBudgetLimit(EXPENSE_DETAILS.currency(), BigDecimal.TEN, test);

        Mockito.verify(underTestSpied, Mockito.times(0))
               .sumAllExpensesByCurrency(test.budgetDetails().defaultCurrency(), test);
    }

    @Test
    void CheckBudgetLimit__When_expense_proper__Should_validate() {
        DefaultInnerExpenseServiceLogic underTestSpied = Mockito.spy(
                new DefaultInnerExpenseServiceLogic(budgetRepository, currencyRepository));

        underTestSpied.checkBudgetLimit(EXPENSE_DETAILS.currency(), BigDecimal.ONE, BUDGET);

        Mockito.verify(underTestSpied, Mockito.times(1))
               .sumAllExpensesByCurrency(BUDGET.budgetDetails().defaultCurrency(), BUDGET);

        Mockito.verify(underTestSpied, Mockito.times(2))
               .getConversionCurrencyRatio(EXPENSE_DETAILS.currency(), BUDGET.budgetDetails().defaultCurrency());
    }

    @Test
    void SumAllExpensesByCurrency__When_expense_proper__Should_validate() {
        BUDGET.budgetDetails().expenseSet().add(MKTCurrency.EUR, BigDecimal.TWO);
        BUDGET.budgetDetails().expenseSet().add(MKTCurrency.EUR, BigDecimal.ONE);

        BigDecimal result = underTest.sumAllExpensesByCurrency(EXPENSE_DETAILS.currency(), BUDGET);

        assertEquals(result, BigDecimal.valueOf(3));
    }

    @Test
    void GetConversionCurrencyRatio__When_same_currencies__Should_return_one() {
        BigDecimal result = underTest.getConversionCurrencyRatio(
                EXPENSE_DETAILS.currency(), EXPENSE_DETAILS.currency());

        assertEquals(result, BigDecimal.ONE);
    }

    @Test
    void GetConversionCurrencyRatio__When_expected_currency_usd__Should_return_ratio() {
        FiatCurrencyDailyData mockFiat = mock(FiatCurrencyDailyData.class);
        List<FiatCurrencyDailyData> mockFiatList = mock(List.class);

        Mockito.when(currencyRepository.findAll())
               .thenReturn(mockFiatList);
        Mockito.when(mockFiatList.getFirst()).thenReturn(mockFiat);
        Mockito.when(mockFiat.conversionRates()).thenReturn(CONVERSION_RATES);
        BigDecimal conversionRate = CONVERSION_RATES.get(MKTCurrency.EUR);
        BigDecimal expected = BigDecimal.ONE.divide(conversionRate, 4, RoundingMode.HALF_UP);

        BigDecimal result = underTest.getConversionCurrencyRatio(MKTCurrency.EUR, MKTCurrency.USD);

        assertEquals(result, expected);
    }

    @Test
    void GetConversionCurrencyRatio__When_expense_currency_usd__Should_return_ratio() {
        FiatCurrencyDailyData mockFiat = mock(FiatCurrencyDailyData.class);
        List<FiatCurrencyDailyData> mockFiatList = mock(List.class);

        Mockito.when(currencyRepository.findAll()).thenReturn(mockFiatList);
        Mockito.when(mockFiatList.getFirst()).thenReturn(mockFiat);
        Mockito.when(mockFiat.conversionRates()).thenReturn(CONVERSION_RATES);
        BigDecimal conversionRate = CONVERSION_RATES.get(MKTCurrency.EUR);
        BigDecimal expected = conversionRate.divide(BigDecimal.ONE, 4, RoundingMode.HALF_UP);

        BigDecimal result = underTest.getConversionCurrencyRatio(MKTCurrency.USD, MKTCurrency.EUR);

        assertEquals(result, expected);
    }

    //TODO write test for pagable
}
