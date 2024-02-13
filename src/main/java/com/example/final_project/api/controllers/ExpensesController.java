package com.example.final_project.api.controllers;

import com.example.final_project.api.requests.expenses.RegisterExpenseRequest;
import com.example.final_project.api.requests.expenses.UpdateExpenseRequest;
import com.example.final_project.api.responses.expenses.ExpenseResponseDto;
import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.expenses.*;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.users.UserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static com.example.final_project.api.controllers.ExpensesController.EXPENSES_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(EXPENSES_BASE_PATH)
public class ExpensesController {
    public static final String EXPENSES_BASE_PATH = "/expenses";
    private final ExpensesService expensesService;
    private final JwtService jwtService;

    @GetMapping("/{rawExpenseId}")
    ResponseEntity<ExpenseResponseDto> getSingleExpense(
            @PathVariable String rawExpenseId,
            Authentication authentication
    ) {
        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Optional<Expense> expenseById = expensesService.getExpenseById(new ExpenseId(rawExpenseId), userId);
        return ResponseEntity.of(expenseById.map(ExpenseResponseDto::fromDomain));
    }

    @GetMapping
    ResponseEntity<Page<ExpenseResponseDto>> getExpensesByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "expenseId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {
        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);
        return ResponseEntity.ok(expensesService.findAllByPage(
                                                        PageRequest.of(page, size, Sort.by(sortDirection, sortBy)), userId)
                                                .map(ExpenseResponseDto::fromDomain));
    }

    @GetMapping("/budget/{rawBudgetId}")
    ResponseEntity<Page<ExpenseResponseDto>> getExpensesByBudgetId(
            @PathVariable String rawBudgetId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {

        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);

        return ResponseEntity.ok(expensesService.findAllExpensesByBudgetId(
                                                        userId,
                                                        BudgetId.newFromString(rawBudgetId),
                                                        PageRequest.of(page, size,
                                                                       Sort.by(sortDirection, sortBy)
                                                        )
                                                )
                                                .map(ExpenseResponseDto::fromDomain));
    }

    @PostMapping
    ResponseEntity<ExpenseResponseDto> registerNewExpense(
            @RequestBody @Valid RegisterExpenseRequest request,
            Authentication authentication
    ) {

        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);

        Expense newExpense = expensesService.registerNewExpense(request.title(), request.amount(),
                                                                BudgetId.newFromString(request.budgetId()), userId,
                                                                request.typeOfExpense()
        );
        ExpenseResponseDto expenseResponseDto = ExpenseResponseDto.fromDomain(newExpense);
        return ResponseEntity.created(URI.create("/expenses/" + expenseResponseDto.expenseId()))
                             .body(expenseResponseDto);
    }

    @DeleteMapping("/{rawExpenseId}")
    public ResponseEntity<ExpenseResponseDto> deleteExpense(
            @PathVariable String rawExpenseId,
            Authentication authentication
    ) {

        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);

        expensesService.getExpenseById(new ExpenseId(rawExpenseId), userId)
                       .ifPresent(expense -> expensesService.deleteExpenseById(expense.expenseId(), userId));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{rawExpenseId}")
    public ResponseEntity<ExpenseResponseDto> updateExpenseField(
            @PathVariable UUID rawExpenseId,
            @RequestBody UpdateExpenseRequest request,
            Authentication authentication
    ) {

        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);

        Optional<BigDecimal> amount = (Optional.ofNullable(request.amount()));
        Optional<String> title = Optional.ofNullable(request.title());

        return ResponseEntity.of(expensesService.updateExpenseContent(
                                                        new ExpenseId(rawExpenseId.toString()), title, amount, userId, request.typeOfExpense())
                                                .map(ExpenseResponseDto::fromDomain));
    }

    @PutMapping("/{rawExpenseId}")
    public ResponseEntity<ExpenseResponseDto> updateExpense(
            @PathVariable UUID rawExpenseId,
            @RequestBody RegisterExpenseRequest request,
            Authentication authentication
    ) {

        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);

        Expense updatedExpense = expensesService.updateExpenseById(
                ExpenseId.newId(rawExpenseId.toString()),
                BudgetId.newFromString(request.budgetId()),
                request.title(),
                request.amount(),
                userId,
                request.typeOfExpense().orElse(TypeOfExpense.NO_CATEGORY)
        );

        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(updatedExpense));
    }
}
