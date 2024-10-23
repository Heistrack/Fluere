package com.example.fluere.budget.service.user;

import com.example.fluere.budget.model.*;
import com.example.fluere.budget.repository.BudgetRepository;
import com.example.fluere.budget.response.BudgetUserMoneySavedDTO;
import com.example.fluere.currencyapi.model.MKTCurrency;
import com.example.fluere.expense.model.Expense;
import com.example.fluere.expense.model.ExpenseType;
import com.example.fluere.expense.service.user.ExpenseInnerServiceLogic;
import com.example.fluere.userentity.model.UserIdWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class DefaultInnerBudgetServiceLogic implements BudgetInnerServiceLogic {
    private final BudgetRepository budgetRepository;
    private final ExpenseInnerServiceLogic expenseInnerServiceLogic;

    @Override
    public String duplicateBudgetTitleCheck(String title, UserIdWrapper userId) {
        if (budgetRepository.existsByUserIdAndBudgetDetails_Title(userId, title)) {
            long counter = 0;
            StringBuilder stringBuilder = new StringBuilder(title);
            while (budgetRepository.existsByUserIdAndBudgetDetails_Title(userId, stringBuilder.toString())) {
                counter++;
                stringBuilder = new StringBuilder(title);
                stringBuilder.append("(").append(counter).append(")");
            }
            return stringBuilder.toString();
        }
        return title;
    }

    @Override
    public void updateHistoryChange(Budget oldBudget) {
        TreeMap<Integer, LocalDateTime> history = oldBudget.budgetDetails().historyOfChanges();
        Integer newRecordNumber = history.lastEntry().getKey() + 1;
        history.put(newRecordNumber, LocalDateTime.now());
    }

    @Override
    public BudgetPeriod getBudgetPeriod(LocalDate startTime, LocalDate endTime) {
        if (startTime == null) {
            LocalDate now = LocalDate.now();
            startTime = LocalDate.of(now.getYear(), now.getMonth().getValue(), 1);
        }
        if (endTime == null) {
            LocalDate now = LocalDate.now();
            endTime = LocalDate.of(now.getYear(), now.getMonth().getValue(), now.lengthOfMonth());
        }

        return BudgetPeriod.newOf(startTime, endTime);
    }

    @Override
    public TreeMap<LocalDate, List<Expense>> getExpensesByDay(List<Expense> expenses) {
        return expenses.stream().collect(Collectors.groupingBy(
                exp -> exp.expenseDetails()
                          .historyOfChanges()
                          .firstEntry()
                          .getValue()
                          .toLocalDate(), TreeMap::new, Collectors.toList()));
    }

    @Override
    public HashMap<ExpenseType, List<Expense>> getExpensesByCategory(List<Expense> expenses) {
        return expenses.stream().collect(
                Collectors.groupingBy(exp -> exp.expenseDetails().expenseType(), HashMap::new, Collectors.toList()));
    }

    @Override
    public HashMap<ExpenseType, Float> getExpenseCategoryPercentage(List<Expense> expenses, Budget budget) {
        HashMap<ExpenseType, List<Expense>> expensesByCategory = getExpensesByCategory(expenses);
        BigDecimal limit = budget.budgetDetails().limit();

        return new HashMap<>(expensesByCategory
                                     .entrySet()
                                     .stream()
                                     .collect(Collectors
                                                      .toMap(
                                                              Map.Entry::getKey,
                                                              val -> budgetFullFillPercentage(limit, val.getValue()
                                                                                                        .stream()
                                                                                                        .map(exp -> exp
                                                                                                                .expenseDetails()
                                                                                                                .amount())
                                                                                                        .reduce(
                                                                                                                BigDecimal.ZERO,
                                                                                                                BigDecimal::add
                                                                                                        )
                                                              )
                                                      )));
    }

    @Override
    public Float budgetFullFillPercentage(BigDecimal base, BigDecimal actual) {
        return actual.multiply(BigDecimal.valueOf(100)).divide(base, 1, RoundingMode.DOWN).floatValue();
    }

    @Override
    public String getTrueLimitFromBudget(Budget budget) {
        BudgetType ourBudgetType = budget.budgetDetails().budgetType();
        BigDecimal limit = budget.budgetDetails().limit().multiply(ourBudgetType.getValue());
        if (!ourBudgetType.getValue().equals(BigDecimal.valueOf(-1))) {
            return limit.toString();
        } else {
            return "no limit";
        }
    }

    @Override
    public BudgetUserMoneySavedDTO getMoneySavedBySingleUser(UserIdWrapper userId) {
        List<Budget> allBudgetsByUserId = budgetRepository.findAllByUserId(userId);
        LocalDate now = LocalDate.now();
        List<Budget> closedBudgets = allBudgetsByUserId.stream().filter(budget -> budget.budgetDetails().budgetPeriod()
                                                                                        .getEndTime()
                                                                                        .isBefore(now)).toList();
        BigDecimal sum = BigDecimal.ZERO;
        for (Budget budget : closedBudgets) {
            sum = sum.add(showBalanceByCurrency(budget.budgetDetails().defaultCurrency(), budget));
        }
        return BudgetUserMoneySavedDTO.newOf(userId.toString(), sum);
    }

    @Override
    public boolean noParamChangeCheck(Budget oldBudget,
                                      Optional<String> newTitle,
                                      Optional<BigDecimal> newLimit,
                                      Optional<BudgetType> newBudgetType,
                                      Optional<BigDecimal> newMaxSingleExpense,
                                      Optional<MKTCurrency> newDefaultCurrency,
                                      BudgetPeriod newBudgetPeriod,
                                      Optional<String> newDescription
    ) {
        BudgetDetails oldBudgetDetails = oldBudget.budgetDetails();
        if (newTitle.isPresent() && !oldBudgetDetails.title().equals(newTitle.get())) return false;
        if (newLimit.isPresent() && !oldBudgetDetails.limit().equals(newLimit.get())) return false;
        if (newBudgetType.isPresent() && !oldBudgetDetails.budgetType().equals(newBudgetType.get()))
            return false;
        if (newMaxSingleExpense.isPresent() && !oldBudgetDetails.maxSingleExpense().equals(newMaxSingleExpense.get()))
            return false;
        if (newDefaultCurrency.isPresent() && !oldBudgetDetails.defaultCurrency()
                                                               .equals(newDefaultCurrency.get()))
            return false;
        if (newDescription.isPresent() && !oldBudgetDetails.description().equals(newDescription.get()))
            return false;
        if (!oldBudgetDetails.budgetPeriod().equals(newBudgetPeriod))
            return false;

        return true;
    }

    @Override
    public BigDecimal showBalanceByCurrency(MKTCurrency expectedCurrency, Budget budget) {
        return expenseInnerServiceLogic.sumAllExpensesByCurrency(expectedCurrency, budget);
    }

    @Override
    public <T extends LinkableDTO> PagedModel<T> getPagedModel(Page<T> linkableDTOs, Class<T> classCast,
                                                               Class<?> controllerClass
    ) {
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                linkableDTOs.getSize(),
                linkableDTOs.getNumber(),
                linkableDTOs.getTotalElements(),
                linkableDTOs.getTotalPages()
        );
        List<T> list = linkableDTOs.stream().toList();
        Link generalLink = linkTo(controllerClass).slash(linkableDTOs.getNumber() + 1).withSelfRel();
        list.forEach(dto -> dto.addLink(
                linkTo(controllerClass).slash(linkableDTOs.getNumber() + 1).slash(dto.PathMessage()).withSelfRel()));
        list.forEach(classCast::cast);
        return PagedModel.of(list, pageMetadata, generalLink);
    }
}
