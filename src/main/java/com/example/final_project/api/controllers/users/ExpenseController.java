package com.example.final_project.api.controllers.users;

import com.example.final_project.api.requests.expenses.appusers.PatchExpenseRequest;
import com.example.final_project.api.requests.expenses.appusers.RegisterExpenseRequest;
import com.example.final_project.api.requests.expenses.appusers.UpdateExpenseRequest;
import com.example.final_project.api.responses.expenses.appusers.ExpenseResponseDto;
import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseIdWrapper;
import com.example.final_project.domain.expenses.ExpensesService;
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

import static com.example.final_project.api.controllers.users.ExpenseController.EXPENSES_CONTROLLER_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(EXPENSES_CONTROLLER_BASE_PATH)
public class ExpenseController {
    public static final String EXPENSES_CONTROLLER_BASE_PATH = "/api/expenses";
    private final ExpensesService expensesService;

    @PostMapping
    ResponseEntity<ExpenseResponseDto> registerNewExpense(
            @RequestBody @Valid RegisterExpenseRequest request,
            Authentication authentication
    ) {
        Expense newExpense = expensesService.registerNewExpense(request.title(), request.amount(),
                                                                BudgetIdWrapper.newFromString(request.budgetId()),
                                                                authentication,
                                                                request.expenseType()
        );
        ExpenseResponseDto response = ExpenseResponseDto.fromDomain(newExpense);
        return ResponseEntity.created(URI.create("/expenses/" + response.expenseId()))
                             .body(response);
    }

    @GetMapping("/{rawexpenseid}")
    ResponseEntity<ExpenseResponseDto> getSingleExpense(
            @PathVariable(name = "rawexpenseid") UUID rawExpenseId,
            Authentication authentication
    ) {
        Expense expenseById = expensesService.getExpenseById(ExpenseIdWrapper.newOf(rawExpenseId), authentication);
        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(expenseById));
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
        return ResponseEntity.ok(expensesService.getAllExpensesByBudgetId(
                                                        authentication,
                                                        BudgetIdWrapper.newOf(rawBudgetId),
                                                        PageRequest.of(page, size,
                                                                       Sort.by(sortDirection, sortBy)
                                                        )
                                                )
                                                .map(ExpenseResponseDto::fromDomain));
    }

    @GetMapping
    ResponseEntity<Page<ExpenseResponseDto>> getExpensesByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {
        return ResponseEntity.ok(expensesService.getAllByPage(
                                                        authentication,
                                                        PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
                                                )
                                                .map(ExpenseResponseDto::fromDomain));
    }

    @PatchMapping()
    ResponseEntity<ExpenseResponseDto> updateExpenseField(
            @RequestBody @Valid PatchExpenseRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(expensesService.patchExpenseContent(
                ExpenseIdWrapper.newOf(UUID.fromString(request.expenseId())),
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.amount()),
                authentication,
                Optional.ofNullable(request.expenseType())
        )));
    }

    @PutMapping()
    ResponseEntity<ExpenseResponseDto> updateExpense(
            @RequestBody @Valid UpdateExpenseRequest request,
            Authentication authentication
    ) {
        Expense updatedExpense = expensesService.updateExpenseById(
                ExpenseIdWrapper.newOf(UUID.fromString(request.expenseId())),
                request.title(),
                request.amount(),
                authentication,
                Optional.ofNullable(request.expenseType())
        );
        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(updatedExpense));
    }

    @DeleteMapping("/{rawexpenseid}")
    ResponseEntity<ExpenseResponseDto> deleteExpense(
            @PathVariable(name = "rawexpenseid") UUID rawExpenseId,
            Authentication authentication
    ) {
        expensesService.deleteExpenseById(ExpenseIdWrapper.newOf(rawExpenseId), authentication);
        return ResponseEntity.noContent().build();
    }
}
