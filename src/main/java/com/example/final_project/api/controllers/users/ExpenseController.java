package com.example.final_project.api.controllers.users;

import com.example.final_project.api.requests.expenses.PatchExpenseRequest;
import com.example.final_project.api.requests.expenses.RegisterExpenseRequest;
import com.example.final_project.api.requests.expenses.UpdateExpenseRequest;
import com.example.final_project.api.responses.expenses.ExpenseResponseDto;
import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseIdWrapper;
import com.example.final_project.domain.expenses.ExpensesService;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static com.example.final_project.api.controllers.users.ExpenseController.EXPENSES_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(EXPENSES_BASE_PATH)
public class ExpenseController {
    public static final String EXPENSES_BASE_PATH = "/expenses";
    private final ExpensesService expensesService;
    private final JwtService jwtService;

    @GetMapping("/{rawexpenseid}")
    ResponseEntity<ExpenseResponseDto> getSingleExpense(
            @PathVariable(name = "rawexpenseid") UUID rawExpenseId,
            Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Expense expenseById = expensesService.getExpenseById(new ExpenseIdWrapper(rawExpenseId), userId);
        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(expenseById));
    }

    @DeleteMapping("/{rawexpenseid}")
    ResponseEntity<ExpenseResponseDto> deleteExpense(
            @PathVariable(name = "rawexpenseid") UUID rawExpenseId,
            Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);

        expensesService.deleteExpenseById(ExpenseIdWrapper.newOf(rawExpenseId), userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    ResponseEntity<ExpenseResponseDto> registerNewExpense(
            @RequestBody @Valid RegisterExpenseRequest request,
            Authentication authentication
    ) {

        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);

        Expense newExpense = expensesService.registerNewExpense(request.title(), request.amount(),
                                                                BudgetIdWrapper.newFromString(request.budgetId()),
                                                                userId,
                                                                request.expenseType()
        );
        ExpenseResponseDto response = ExpenseResponseDto.fromDomain(newExpense);
        return ResponseEntity.created(URI.create("/expenses/" + response.expenseId()))
                             .body(response);
    }

    @PatchMapping()
    ResponseEntity<ExpenseResponseDto> updateExpenseField(
            @RequestBody @Valid PatchExpenseRequest request,
            Authentication authentication
    ) {

        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);

        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(expensesService.patchExpenseContent(
                ExpenseIdWrapper.newOf(UUID.fromString(request.expenseId())),
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.amount()),
                userId,
                Optional.ofNullable(request.expenseType())
        )));
    }

    @PutMapping()
    ResponseEntity<ExpenseResponseDto> updateExpense(
            @RequestBody @Valid UpdateExpenseRequest request,
            Authentication authentication
    ) {

        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);

        Expense updatedExpense = expensesService.updateExpenseById(
                ExpenseIdWrapper.newOf(UUID.fromString(request.expenseId())),
                request.title(),
                request.amount(),
                userId,
                Optional.ofNullable(request.expenseType())
        );

        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(updatedExpense));
    }

    @GetMapping
    ResponseEntity<Page<ExpenseResponseDto>> getExpensesByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        return ResponseEntity.ok(expensesService.findAllByPage(
                                                        userId,
                                                        PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
                                                )
                                                .map(ExpenseResponseDto::fromDomain));
    }

    @GetMapping("/budget/{rawbudgetid}")
    ResponseEntity<Page<ExpenseResponseDto>> getExpensesByBudgetId(
            @PathVariable(name = "rawbudgetid") UUID rawBudgetId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {

        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);

        return ResponseEntity.ok(expensesService.findAllExpensesByBudgetId(
                                                        userId,
                                                        BudgetIdWrapper.newOf(rawBudgetId),
                                                        PageRequest.of(page, size,
                                                                       Sort.by(sortDirection, sortBy)
                                                        )
                                                )
                                                .map(ExpenseResponseDto::fromDomain));
    }
}
