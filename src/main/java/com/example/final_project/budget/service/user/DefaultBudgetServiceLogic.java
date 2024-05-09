package com.example.final_project.budget.service.user;

import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.BudgetDetails;
import com.example.final_project.budget.model.BudgetPeriod;
import com.example.final_project.budget.model.BudgetType;
import com.example.final_project.budget.repository.BudgetRepository;
import com.example.final_project.budget.response.BudgetResponseDto;
import com.example.final_project.currencyapi.model.MKTCurrency;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseType;
import com.example.final_project.expense.response.ExpenseResponseDto;
import com.example.final_project.expense.service.user.ExpenseServiceLogic;
import com.example.final_project.userentity.model.UserIdWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
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
public class DefaultBudgetServiceLogic implements BudgetServiceLogic {
    private final BudgetRepository budgetRepository;
    private final ExpenseServiceLogic expenseServiceLogic;

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
        return expenseServiceLogic.sumAllExpensesByCurrency(expectedCurrency, budget);
    }
    //TODO extend HATEOAS links to more road signs
    @Override
    public EntityModel<BudgetResponseDto> getEntityModelFromLink(Link link, Budget budget) {
        BudgetResponseDto budgetResponseDto = BudgetResponseDto.fromDomain(budget);
        return EntityModel.of(budgetResponseDto.add(link));
    }

    @Override
    public PagedModel<BudgetResponseDto> getPagedModel(Link generalLink, Class<?> controller, Page<Budget> budgets) {
        List<BudgetResponseDto> list = budgets.map(BudgetResponseDto::fromDomain).stream().toList();
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(budgets.getSize(), budgets.getNumber(),
                                                                           budgets.getTotalElements(),
                                                                           budgets.getTotalPages());
        list.forEach(dto -> dto.add(linkTo(controller).slash(dto.getBudgetId()).withSelfRel()));
        return PagedModel.of(list, pageMetadata, generalLink);
    }
}
