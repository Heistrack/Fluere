package com.example.final_project.expense.controller.user;

import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseIdWrapper;
import com.example.final_project.expense.request.appuser.PatchExpenseRequest;
import com.example.final_project.expense.request.appuser.RegisterExpenseRequest;
import com.example.final_project.expense.request.appuser.UpdateExpenseRequest;
import com.example.final_project.expense.response.appuser.ExpenseResponseDto;
import com.example.final_project.expense.service.user.ExpenseService;
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

import static com.example.final_project.expense.controller.user.ExpenseController.EXPENSES_CONTROLLER_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(EXPENSES_CONTROLLER_BASE_PATH)
public class ExpenseController {
    public static final String EXPENSES_CONTROLLER_BASE_PATH = "/api/expenses";
    private final ExpenseService expenseService;

    @PostMapping
    ResponseEntity<ExpenseResponseDto> registerNewExpense(
            @RequestBody @Valid RegisterExpenseRequest request,
            Authentication authentication
    ) {
        Expense newExpense = expenseService.registerNewExpense(BudgetIdWrapper.newFromString(request.budgetId()),
                                                               request.title(), request.amount(),
                                                               request.currency(),
                                                               request.expenseType(),
                                                               request.description(),
                                                               authentication
        );
        ExpenseResponseDto response = ExpenseResponseDto.fromDomain(newExpense);
        return ResponseEntity.created(URI.create("/expenses/" + response.expenseId()))
                             .body(response);
    }

    @GetMapping("/{expense_uuid}")
    ResponseEntity<ExpenseResponseDto> getSingleExpense(
            @PathVariable(name = "expense_uuid") UUID expenseUUID,
            Authentication authentication
    ) {
        Expense expenseById = expenseService.getExpenseById(ExpenseIdWrapper.newOf(expenseUUID), authentication);
        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(expenseById));
    }

    @GetMapping("/budget/{budget_uuid}")
    ResponseEntity<Page<ExpenseResponseDto>> getExpensesByBudgetId(
            @PathVariable(name = "budget_uuid") UUID budgetUUID,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {
        return ResponseEntity.ok(expenseService.getAllExpensesByBudgetId(
                                                       BudgetIdWrapper.newOf(budgetUUID),
                                                       PageRequest.of(page, size,
                                                                      Sort.by(sortDirection, sortBy)
                                                       ), authentication
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
        return ResponseEntity.ok(expenseService.getAllByPage(
                                                       PageRequest.of(page, size, Sort.by(sortDirection, sortBy)),
                                                       authentication
                                               )
                                               .map(ExpenseResponseDto::fromDomain));
    }

    @PatchMapping()
    ResponseEntity<ExpenseResponseDto> patchExpenseField(
            @RequestBody @Valid PatchExpenseRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(expenseService.patchExpenseContent(
                ExpenseIdWrapper.newOf(UUID.fromString(request.expenseId())),
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.amount()),
                Optional.ofNullable(request.currency()),
                Optional.ofNullable(request.expenseType()),
                Optional.ofNullable(request.description()),
                authentication
        )));
    }

    @PutMapping()
    ResponseEntity<ExpenseResponseDto> updateExpense(
            @RequestBody @Valid UpdateExpenseRequest request,
            Authentication authentication
    ) {
        Expense updatedExpense = expenseService.updateExpenseById(
                ExpenseIdWrapper.newOf(UUID.fromString(request.expenseId())),
                request.title(),
                request.amount(),
                request.currency(),
                request.expenseType(),
                request.description(),
                authentication
        );
        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(updatedExpense));
    }

    @DeleteMapping("/{expense_uuid}")
    ResponseEntity<ExpenseResponseDto> deleteExpense(
            @PathVariable(name = "expense_uuid") UUID expenseUUID,
            Authentication authentication
    ) {
        expenseService.deleteExpenseById(ExpenseIdWrapper.newOf(expenseUUID), authentication);
        return ResponseEntity.noContent().build();
    }
}
