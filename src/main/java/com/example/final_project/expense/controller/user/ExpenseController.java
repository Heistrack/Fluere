package com.example.final_project.expense.controller.user;

import com.example.final_project.expense.request.appuser.PatchExpenseRequest;
import com.example.final_project.expense.request.appuser.RegisterExpenseRequest;
import com.example.final_project.expense.request.appuser.UpdateExpenseRequest;
import com.example.final_project.expense.response.appuser.ExpenseResponseDto;
import com.example.final_project.budget.service.BudgetIdWrapper;
import com.example.final_project.expense.service.Expense;
import com.example.final_project.expense.service.ExpenseIdWrapper;
import com.example.final_project.expense.service.user.ExpensesService;
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
    private final ExpensesService expensesService;

    @PostMapping
    ResponseEntity<ExpenseResponseDto> registerNewExpense(
            @RequestBody @Valid RegisterExpenseRequest request,
            Authentication authentication
    ) {
        Expense newExpense = expensesService.registerNewExpense(request.title(), request.amount(),
                                                                BudgetIdWrapper.newFromString(request.budgetId()),
                                                                authentication,
                                                                request.expenseType(),
                                                                request.description()
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
        Expense expenseById = expensesService.getExpenseById(ExpenseIdWrapper.newOf(expenseUUID), authentication);
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
        return ResponseEntity.ok(expensesService.getAllExpensesByBudgetId(
                                                        authentication,
                                                        BudgetIdWrapper.newOf(budgetUUID),
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
    ResponseEntity<ExpenseResponseDto> patchExpenseField(
            @RequestBody @Valid PatchExpenseRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(expensesService.patchExpenseContent(
                ExpenseIdWrapper.newOf(UUID.fromString(request.expenseId())),
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.amount()),
                authentication,
                Optional.ofNullable(request.expenseType()),
                Optional.ofNullable(request.description())
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
                Optional.ofNullable(request.expenseType()),
                Optional.ofNullable(request.description())
        );
        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(updatedExpense));
    }

    @DeleteMapping("/{expense_uuid}")
    ResponseEntity<ExpenseResponseDto> deleteExpense(
            @PathVariable(name = "expense_uuid") UUID expenseUUID,
            Authentication authentication
    ) {
        expensesService.deleteExpenseById(ExpenseIdWrapper.newOf(expenseUUID), authentication);
        return ResponseEntity.noContent().build();
    }
}
