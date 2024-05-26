package com.example.final_project.expense.controller.user;

import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseIdWrapper;
import com.example.final_project.expense.request.appuser.PatchExpenseRequest;
import com.example.final_project.expense.request.appuser.RegisterExpenseRequest;
import com.example.final_project.expense.request.appuser.UpdateExpenseRequest;
import com.example.final_project.expense.response.ExpenseResponseDto;
import com.example.final_project.expense.service.user.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    ResponseEntity<EntityModel<ExpenseResponseDto>> registerNewExpense(
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
        return ResponseEntity.status(201).body(expenseService.getEntityModel(
                ExpenseResponseDto.fromDomain(newExpense),
                ExpenseResponseDto.class
        ));
    }

    @GetMapping("/{expense_uuid}")
    ResponseEntity<EntityModel<ExpenseResponseDto>> getSingleExpense(
            @PathVariable(name = "expense_uuid") UUID expenseUUID,
            Authentication authentication
    ) {
        Expense expense = expenseService.getExpenseById(ExpenseIdWrapper.newOf(expenseUUID), authentication);
        return ResponseEntity.ok(
                expenseService.getEntityModel(ExpenseResponseDto.fromDomain(expense), ExpenseResponseDto.class));
    }

    @GetMapping("/budget/{budget_uuid}")
    ResponseEntity<PagedModel<ExpenseResponseDto>> getExpensesByBudgetId(
            @PathVariable(name = "budget_uuid") UUID budgetUUID,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {
        Page<ExpenseResponseDto> allExpensesByBudgetId = expenseService.getAllExpensesByBudgetId(
                BudgetIdWrapper.newOf(budgetUUID),
                PageRequest.of(page, size,
                               Sort.by(sortDirection, sortBy)
                ), authentication
        ).map(ExpenseResponseDto::fromDomain);
        return ResponseEntity.ok(expenseService.getEntities(allExpensesByBudgetId, ExpenseResponseDto.class));
    }

    @GetMapping
    ResponseEntity<PagedModel<ExpenseResponseDto>> getExpensesByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {
        Page<ExpenseResponseDto> allByPage = expenseService.getAllByPage(
                PageRequest.of(page, size, Sort.by(sortDirection, sortBy)),
                authentication
        ).map(ExpenseResponseDto::fromDomain);
        return ResponseEntity.ok(expenseService.getEntities(allByPage, ExpenseResponseDto.class));
    }

    @PatchMapping()
    ResponseEntity<EntityModel<ExpenseResponseDto>> patchExpenseField(
            @RequestBody @Valid PatchExpenseRequest request,
            Authentication authentication
    ) {
        Expense patchedExpense = expenseService.patchExpenseContent(
                ExpenseIdWrapper.newOf(UUID.fromString(request.expenseId())),
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.amount()),
                Optional.ofNullable(request.currency()),
                Optional.ofNullable(request.expenseType()),
                Optional.ofNullable(request.description()),
                authentication
        );
        return ResponseEntity.ok(
                expenseService.getEntityModel(ExpenseResponseDto.fromDomain(patchedExpense), ExpenseResponseDto.class));
    }

    @PutMapping()
    ResponseEntity<EntityModel<ExpenseResponseDto>> updateExpense(
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
        return ResponseEntity.ok(
                expenseService.getEntityModel(ExpenseResponseDto.fromDomain(updatedExpense), ExpenseResponseDto.class));
    }

    @DeleteMapping("/{expense_uuid}")
    ResponseEntity<EntityModel<ExpenseResponseDto>> deleteExpense(
            @PathVariable(name = "expense_uuid") UUID expenseUUID,
            Authentication authentication
    ) {
        expenseService.deleteExpenseById(ExpenseIdWrapper.newOf(expenseUUID), authentication);
        return ResponseEntity.noContent().build();
    }
}
