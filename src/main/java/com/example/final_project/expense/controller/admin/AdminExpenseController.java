package com.example.final_project.expense.controller.admin;

import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.expense.model.Expense;
import com.example.final_project.expense.model.ExpenseIdWrapper;
import com.example.final_project.expense.request.admin.AdminRegisterExpenseRequest;
import com.example.final_project.expense.request.appuser.PatchExpenseRequest;
import com.example.final_project.expense.request.appuser.UpdateExpenseRequest;
import com.example.final_project.expense.response.ExpenseResponseDto;
import com.example.final_project.expense.service.admin.AdminExpenseService;
import com.example.final_project.userentity.model.UserIdWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static com.example.final_project.expense.controller.admin.AdminExpenseController.ADMIN_EXPENSE_CONTROLLERS_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(ADMIN_EXPENSE_CONTROLLERS_BASE_PATH)
public class AdminExpenseController {
    public static final String ADMIN_EXPENSE_CONTROLLERS_BASE_PATH = "/api/x/expenses";
    private final AdminExpenseService adminExpenseService;

    @PostMapping
    ResponseEntity<EntityModel<ExpenseResponseDto>> registerNewExpense(
            @RequestBody @Valid AdminRegisterExpenseRequest request
    ) {
        Expense newExpense = adminExpenseService.registerNewExpense(
                BudgetIdWrapper.newFromString(request.budgetId()),
                request.title(),
                request.amount(),
                request.currency(),
                request.expenseType(),
                request.description()
        );
        return ResponseEntity.status(201).body(adminExpenseService.getEntityModel(newExpense));
    }


    @GetMapping("/{expense_uuid}")
    ResponseEntity<EntityModel<ExpenseResponseDto>> getSingleExpense(
            @PathVariable(name = "expense_uuid") UUID expenseUUID
    ) {
        Expense expense = adminExpenseService.getExpenseById(ExpenseIdWrapper.newOf(expenseUUID));

        return ResponseEntity.ok(adminExpenseService.getEntityModel(expense));
    }

    @GetMapping("/budget/{budget_uuid}")
    ResponseEntity<PagedModel<ExpenseResponseDto>> getAllExpensesByBudgetId(
            @PathVariable(name = "budget_uuid") UUID budgetUUID,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        Page<Expense> expenses = adminExpenseService.getAllExpensesByBudgetId(
                BudgetIdWrapper.newOf(budgetUUID),
                PageRequest.of(page, size,
                               Sort.by(sortDirection, sortBy)
                )
        );
        return ResponseEntity.ok(adminExpenseService.getEntities(expenses));
    }

    @GetMapping("/users/{user_uuid}")
    ResponseEntity<PagedModel<ExpenseResponseDto>> getAllExpensesByUserId(
            @PathVariable(name = "user_uuid") UUID userUUID,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        Page<Expense> expenses = adminExpenseService.getAllExpensesByUserId(
                UserIdWrapper.newOf(userUUID),
                PageRequest.of(page, size,
                               Sort.by(sortDirection, sortBy)
                )
        );
        return ResponseEntity.ok(adminExpenseService.getEntities(expenses));
    }

    @GetMapping
    ResponseEntity<PagedModel<ExpenseResponseDto>> getAllExpensesByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        Page<Expense> expenses = adminExpenseService.getAllExpensesByPage(
                PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
        );
        return ResponseEntity.ok(adminExpenseService.getEntities(expenses));
    }

    @PutMapping()
    ResponseEntity<EntityModel<ExpenseResponseDto>> updateExpense(
            @RequestBody @Valid UpdateExpenseRequest request
    ) {
        Expense expense = adminExpenseService.updateExpenseById(
                ExpenseIdWrapper.newOf(UUID.fromString(request.expenseId())),
                request.title(),
                request.amount(),
                request.currency(),
                request.expenseType(),
                request.description()
        );
        return ResponseEntity.ok(adminExpenseService.getEntityModel(expense));
    }

    @PatchMapping()
    ResponseEntity<EntityModel<ExpenseResponseDto>> patchExpenseField(
            @RequestBody @Valid PatchExpenseRequest request
    ) {
        Expense expense = adminExpenseService.patchExpenseContent(
                ExpenseIdWrapper.newOf(UUID.fromString(request.expenseId())),
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.amount()),
                Optional.ofNullable(request.currency()),
                Optional.ofNullable(request.expenseType()),
                Optional.ofNullable(request.description())
        );
        return ResponseEntity.ok(adminExpenseService.getEntityModel(expense));
    }

    @DeleteMapping("/{expense_uuid}")
    ResponseEntity<EntityModel<ExpenseResponseDto>> deleteExpense(
            @PathVariable(name = "expense_uuid") UUID expenseUUID
    ) {
        adminExpenseService.deleteExpenseById(ExpenseIdWrapper.newOf(expenseUUID));
        return ResponseEntity.noContent().build();
    }
}
