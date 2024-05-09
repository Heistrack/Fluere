package com.example.final_project.expense.service.user;


import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.budget.repository.BudgetRepository;
import com.example.final_project.currencyapi.model.MKTCurrency;
import com.example.final_project.currencyapi.repository.CurrencyRepository;
import com.example.final_project.exception.custom.ExpenseTooBigException;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseType;
import com.example.final_project.expense.response.ExpenseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class DefaultExpenseServiceLogic implements ExpenseServiceLogic {
    private final BudgetRepository budgetRepository;
    private final CurrencyRepository currencyRepository;

    @Override
    public void balanceUpdate(MKTCurrency currency, BigDecimal amount, Expense oldExpense) {
        Budget budget = budgetRepository.findById(oldExpense.budgetId())
                                        .orElseThrow(() -> new NoSuchElementException("Budget not found."));
        budget.subtractBalance(oldExpense.expenseDetails().currency(), oldExpense.expenseDetails().amount());
        budget.addBalance(currency, amount);
        budgetRepository.save(budget);
    }

    @Override
    public void addBalance(MKTCurrency currency, BigDecimal amount, BudgetIdWrapper budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                                        .orElseThrow(() -> new NoSuchElementException("Budget not found."));
        budget.addBalance(currency, amount);
        budgetRepository.save(budget);
    }

    @Override
    public void updateHistoryChange(Expense oldExpense) {
        TreeMap<Integer, LocalDateTime> history = oldExpense.expenseDetails().historyOfChanges();
        Integer newRecordNumber = history.lastEntry().getKey() + 1;
        history.put(newRecordNumber, LocalDateTime.now());
    }

    @Override
    public boolean noParamChangeCheck(Expense oldExpense,
                                      Optional<String> newTitle,
                                      Optional<BigDecimal> newAmount,
                                      Optional<MKTCurrency> newCurrency,
                                      Optional<ExpenseType> newExpenseType,
                                      Optional<String> newDescription
    ) {
        if (newTitle.isPresent() && !oldExpense.expenseDetails().title().equals(newTitle.get())) return false;
        if (newAmount.isPresent() && !oldExpense.expenseDetails().amount().equals(newAmount.get())) return false;
        if (newCurrency.isPresent() && !oldExpense.expenseDetails().currency().equals(newCurrency.get())) return false;
        if (newExpenseType.isPresent() && !oldExpense.expenseDetails().expenseType().equals(newExpenseType.get()))
            return false;
        if (newDescription.isPresent() && !oldExpense.expenseDetails().description().equals(newDescription.get()))
            return false;

        return true;
    }

    @Override
    public void validationExpenseAmount(MKTCurrency currency, BigDecimal amount, BudgetIdWrapper budgetId) {
        budgetRepository.findById(budgetId).ifPresent((budget) ->
                                                      {
                                                          checkBudgetLimit(currency, amount, budget);
                                                          singleMaxExpValidation(currency, amount, budget);
                                                      });
    }

    @Override
    public void singleMaxExpValidation(MKTCurrency expenseCurrency, BigDecimal amount, Budget budget) {
        MKTCurrency budgetCurrency = budget.budgetDetails().defaultCurrency();
        if (budget.budgetDetails().maxSingleExpense().compareTo(amount.multiply(getConversionCurrencyRatio(
                expenseCurrency,
                budgetCurrency
        ))) < 0) {
            throw new ExpenseTooBigException("Expense exceed single maximal expense amount in the budget!");
        }
    }

    @Override
    public void checkBudgetLimit(MKTCurrency expenseCurrency, BigDecimal amount, Budget budget) {
        if (budget.budgetDetails().budgetType().getValue().compareTo(BigDecimal.ZERO) < 0) {
            return;
        }
        BigDecimal totalBudgetLimit = budget.budgetDetails().limit()
                                            .multiply(budget.budgetDetails().budgetType().getValue());

        BigDecimal totalExpensesSum = sumAllExpensesByCurrency(budget.budgetDetails().defaultCurrency(), budget);

        BigDecimal realAmount = amount.multiply(getConversionCurrencyRatio(expenseCurrency, budget.budgetDetails()
                                                                                                  .defaultCurrency()));
        BigDecimal amountLeft = totalBudgetLimit.subtract(totalExpensesSum);

        if (amountLeft.compareTo(realAmount) < 0) {
            throw new ExpenseTooBigException("Expense exceed the budget limit!");
        }
    }

    @Override
    public BigDecimal sumAllExpensesByCurrency(MKTCurrency expectedCurrency, Budget budget) {
        HashMap<MKTCurrency, BigDecimal> allExpenses = budget.budgetDetails().expenseSet()
                                                             .expenseMap();
        BigDecimal balance = BigDecimal.ZERO;
        for (Map.Entry<MKTCurrency, BigDecimal> expensePair : allExpenses.entrySet()) {
            balance = balance.add(expensePair.getValue().multiply(
                    getConversionCurrencyRatio(expensePair.getKey(), expectedCurrency)));
        }
        return balance;
    }

    @Override
    public BigDecimal getConversionCurrencyRatio(MKTCurrency expenseCurrency, MKTCurrency expectedCurrency) {
        if (expenseCurrency.equals(expectedCurrency)) return BigDecimal.ONE;

        HashMap<MKTCurrency, BigDecimal> conversionRates = currencyRepository.findAll().getFirst().conversionRates();

        BigDecimal expenseCurrencyToUSDRatio = BigDecimal.ONE;
        if (!expenseCurrency.equals(MKTCurrency.USD)) {
            expenseCurrencyToUSDRatio = conversionRates.get(expenseCurrency);
        }

        BigDecimal defaultCurrencyToUSDRatio = BigDecimal.ONE;
        if (!expectedCurrency.equals(MKTCurrency.USD)) {
            defaultCurrencyToUSDRatio = conversionRates.get(expectedCurrency);
        }

        return defaultCurrencyToUSDRatio.divide(expenseCurrencyToUSDRatio, 4, RoundingMode.HALF_UP);
    }

    //TODO extend HATEOAS links to more road signs
    @Override
    public EntityModel<ExpenseResponseDto> getEntityModelFromLink(Link link, Expense expense) {
        ExpenseResponseDto expenseResponseDto = ExpenseResponseDto.fromDomain(expense);
        return EntityModel.of(expenseResponseDto.add(link));
    }

    @Override
    public PagedModel<ExpenseResponseDto> getPagedModel(Link generalLink, Class<?> controller, Page<Expense> expenses) {
        List<ExpenseResponseDto> list = expenses.map(ExpenseResponseDto::fromDomain).stream().toList();
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(expenses.getSize(), expenses.getNumber(),
                                                                           expenses.getTotalElements(),
                                                                           expenses.getTotalPages());
        list.forEach(dto -> dto.add(linkTo(controller).slash(dto.getExpenseId()).withSelfRel()));
        return PagedModel.of(list, pageMetadata, generalLink);
    }
}
