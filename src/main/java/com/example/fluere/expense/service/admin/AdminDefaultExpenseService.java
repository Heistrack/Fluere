package com.example.fluere.expense.service.admin;

import com.example.fluere.budget.model.Budget;
import com.example.fluere.budget.model.BudgetIdWrapper;
import com.example.fluere.budget.model.LinkableDTO;
import com.example.fluere.budget.repository.BudgetRepository;
import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.expense.controller.admin.AdminExpenseController;
import com.example.fluere.expense.model.Expense;
import com.example.fluere.expense.model.ExpenseDetails;
import com.example.fluere.expense.model.ExpenseIdWrapper;
import com.example.fluere.expense.model.ExpenseType;
import com.example.fluere.expense.repository.ExpenseRepository;
import com.example.fluere.expense.service.user.ExpenseInnerServiceLogic;
import com.example.fluere.userentity.model.UserIdWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class AdminDefaultExpenseService implements AdminExpenseService {

    private final ExpenseRepository expenseRepository;
    private final Supplier<ExpenseIdWrapper> expenseIdSupplier;
    private final ExpenseInnerServiceLogic innerServiceLogic;
    private final BudgetRepository budgetRepository;

    @Override
    public Expense registerNewExpense(BudgetIdWrapper budgetId, String title,
                                      BigDecimal amount, MKTCurrency currency, ExpenseType expenseType,
                                      String description
    ) {
        innerServiceLogic.validationExpenseAmount(currency, amount, budgetId);

        TreeMap<Integer, LocalDateTime> historyOfChange = new TreeMap<>();
        historyOfChange.put(1, LocalDateTime.now());

        UserIdWrapper userId = budgetRepository.findById(budgetId).map(Budget::userId)
                                               .orElseThrow(() -> new NoSuchElementException("No user found."));

        Expense expense = Expense.newOf(
                expenseIdSupplier.get(), budgetId, userId, ExpenseDetails.newOf(title, amount,
                                                                                currency,
                                                                                historyOfChange,
                                                                                expenseType,
                                                                                description == null ? "" : description
                ));
        innerServiceLogic.addBalance(currency, amount, budgetId);
        expenseRepository.save(expense);
        return expense;
    }

    @Override
    public Expense getExpenseById(ExpenseIdWrapper expenseId) {
        return expenseRepository.findById(expenseId)
                                .orElseThrow(() -> new NoSuchElementException("Expense doesn't exist."));
    }

    @Override
    public Page<Expense> getAllExpensesByBudgetId(BudgetIdWrapper budgetId, Pageable pageable) {
        return expenseRepository.findAllByBudgetId(budgetId, pageable);
    }

    @Override
    public Page<Expense> getAllExpensesByUserId(UserIdWrapper userId, Pageable pageable) {
        return expenseRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Page<Expense> getAllExpensesByPage(Pageable pageable) {
        return expenseRepository.findAll(pageable);
    }

    @Override
    public Expense updateExpenseById(
            ExpenseIdWrapper expenseId,
            String title,
            BigDecimal amount,
            MKTCurrency currency,
            ExpenseType expenseType,
            String description
    ) {
        Expense oldExpense = expenseRepository.findById(expenseId).orElseThrow(
                () -> new NoSuchElementException("Expense doesn't exist."));

        if (innerServiceLogic.noParamChangeCheck(oldExpense, Optional.of(title),
                                                 Optional.of(amount),
                                                 Optional.of(currency),
                                                 Optional.ofNullable(expenseType),
                                                 Optional.ofNullable(description)
        )) {
            return oldExpense;
        }
        if (!amount.equals(oldExpense.expenseDetails().amount()) || !currency.equals(
                oldExpense.expenseDetails().currency())) {
            innerServiceLogic.validationExpenseAmount(currency, amount, oldExpense.budgetId());
            innerServiceLogic.balanceUpdate(currency, amount, oldExpense);
        }

        innerServiceLogic.updateHistoryChange(oldExpense);

        Expense entity = Expense.newOf(
                expenseId,
                oldExpense.budgetId(),
                oldExpense.userId(),
                ExpenseDetails.newOf(
                        title,
                        amount,
                        currency,
                        oldExpense.expenseDetails().historyOfChanges(),
                        expenseType == null ? ExpenseType.NO_CATEGORY : expenseType,
                        description == null ? "" : description
                )
        );
        expenseRepository.save(entity);
        return entity;
    }

    @Override
    public Expense patchExpenseContent(
            ExpenseIdWrapper expenseId,
            Optional<String> title,
            Optional<BigDecimal> amount,
            Optional<MKTCurrency> currency,
            Optional<ExpenseType> expenseType,
            Optional<String> description
    ) {
        Expense oldExpense = expenseRepository.findById(expenseId)
                                              .orElseThrow(() -> new NoSuchElementException(
                                                      "There's no such expense."));

        if (innerServiceLogic.noParamChangeCheck(oldExpense, title, amount, currency, expenseType, description)) {
            return oldExpense;
        }
        MKTCurrency checkedCurrency = currency.orElse(oldExpense.expenseDetails().currency());
        BigDecimal checkedAmount = amount.orElse(oldExpense.expenseDetails().amount());

        if (!checkedAmount.equals(oldExpense.expenseDetails().amount()) || !checkedCurrency.equals(
                oldExpense.expenseDetails().currency())) {
            innerServiceLogic.validationExpenseAmount(checkedCurrency, checkedAmount, oldExpense.budgetId());
            innerServiceLogic.balanceUpdate(checkedCurrency, checkedAmount, oldExpense);
        }

        innerServiceLogic.updateHistoryChange(oldExpense);

        Expense entity = expenseRepository.findById(expenseId).map(
                expenseFromRepository -> Expense.newOf(
                        expenseId,
                        oldExpense.budgetId(),
                        oldExpense.userId(),
                        ExpenseDetails.newOf(
                                title.orElseGet(() -> expenseFromRepository.expenseDetails().title()),
                                checkedAmount,
                                checkedCurrency,
                                oldExpense.expenseDetails().historyOfChanges(),
                                expenseType.orElseGet(() -> expenseFromRepository.expenseDetails().expenseType()),
                                description.orElseGet(() -> expenseFromRepository.expenseDetails().description())
                        )
                )
        ).orElseThrow(IllegalArgumentException::new);
        expenseRepository.save(entity);
        return entity;
    }

    @Override
    public void deleteExpenseById(ExpenseIdWrapper expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    @Override
    public <T extends LinkableDTO> EntityModel<T> getEntityModel(T linkableDTO, Class<T> classCast) {
        Link link = linkTo(AdminExpenseController.class).slash(linkableDTO.PathMessage()).withSelfRel();
        linkableDTO.addLink(link);
        return EntityModel.of(classCast.cast(linkableDTO));
    }

    @Override
    public <T extends LinkableDTO> PagedModel<T> getEntities(Page<T> linkableDTOs, Class<T> classCast) {
        return innerServiceLogic.getPagedModel(linkableDTOs, classCast, AdminExpenseController.class);
    }
}