package com.example.final_project.api.controllers.admins;

import com.example.final_project.api.requests.expenses.admins.AdminRegisterExpenseRequest;
import com.example.final_project.api.requests.expenses.appusers.PatchExpenseRequest;
import com.example.final_project.api.requests.expenses.appusers.UpdateExpenseRequest;
import com.example.final_project.api.responses.expenses.admins.AdminExpenseResponseDto;
import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseIdWrapper;
import com.example.final_project.domain.expenses.admins.AdminExpenseService;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static com.example.final_project.api.controllers.admins.AdminExpenseController.ADMIN_EXPENSE_CONTROLLERS_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(ADMIN_EXPENSE_CONTROLLERS_BASE_PATH)
public class AdminExpenseController {
    public static final String ADMIN_EXPENSE_CONTROLLERS_BASE_PATH = "/x/expenses";
    private final AdminExpenseService adminExpenseService;

    @PostMapping
    ResponseEntity<AdminExpenseResponseDto> registerNewExpense(
            @RequestBody @Valid AdminRegisterExpenseRequest request
    ) {
        Expense newExpense = adminExpenseService.registerNewExpense(request.title(), request.amount(),
                                                                    BudgetIdWrapper.newFromString(request.budgetId()),
                                                                    request.expenseType(),
                                                                    UserIdWrapper.newFromString(request.userId())
        );
        AdminExpenseResponseDto response = AdminExpenseResponseDto.fromDomain(newExpense);
        return ResponseEntity.created(URI.create("/expenses/" + response.expenseId()))
                             .body(response);
    }

    @GetMapping("/{rawexpenseid}")
    ResponseEntity<AdminExpenseResponseDto> getSingleExpense(
            @PathVariable(name = "rawexpenseid") UUID rawExpenseId
    ) {
        Expense expenseById = adminExpenseService.getExpenseById(ExpenseIdWrapper.newOf(rawExpenseId));
        return ResponseEntity.ok(AdminExpenseResponseDto.fromDomain(expenseById));
    }

    @GetMapping("/budget/{rawbudgetid}")
    ResponseEntity<Page<AdminExpenseResponseDto>> getAllExpensesByBudgetId(
            @PathVariable(name = "rawbudgetid") UUID rawBudgetId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        return ResponseEntity.ok(adminExpenseService.getAllExpensesByBudgetId(
                                                            BudgetIdWrapper.newOf(rawBudgetId),
                                                            PageRequest.of(page, size,
                                                                           Sort.by(sortDirection, sortBy)
                                                            )
                                                    )
                                                    .map(AdminExpenseResponseDto::fromDomain));
    }

    @GetMapping("/users/{userid}")
    ResponseEntity<Page<AdminExpenseResponseDto>> getAllExpensesByUserId(
            @PathVariable(name = "userid") UUID rawUserId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        return ResponseEntity.ok(adminExpenseService.getAllExpensesByUserId(
                                                            UserIdWrapper.newOf(rawUserId),
                                                            PageRequest.of(page, size,
                                                                           Sort.by(sortDirection, sortBy)
                                                            )
                                                    )
                                                    .map(AdminExpenseResponseDto::fromDomain));
    }

    @GetMapping
    ResponseEntity<Page<AdminExpenseResponseDto>> getAllExpensesByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        return ResponseEntity.ok(adminExpenseService.getAllExpensesByPage(
                                                            PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
                                                    )
                                                    .map(AdminExpenseResponseDto::fromDomain));
    }

    @PutMapping()
    ResponseEntity<AdminExpenseResponseDto> updateExpense(
            @RequestBody @Valid UpdateExpenseRequest request
    ) {
        Expense updatedExpense = adminExpenseService.updateExpenseById(
                ExpenseIdWrapper.newOf(UUID.fromString(request.expenseId())),
                request.title(),
                request.amount(),
                Optional.ofNullable(request.expenseType())
        );
        return ResponseEntity.ok(AdminExpenseResponseDto.fromDomain(updatedExpense));
    }

    @PatchMapping()
    ResponseEntity<AdminExpenseResponseDto> updateExpenseField(
            @RequestBody @Valid PatchExpenseRequest request
    ) {
        return ResponseEntity.ok(AdminExpenseResponseDto.fromDomain(adminExpenseService.patchExpenseContent(
                ExpenseIdWrapper.newOf(UUID.fromString(request.expenseId())),
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.amount()),
                Optional.ofNullable(request.expenseType())
        )));
    }

    @DeleteMapping("/{rawexpenseid}")
    ResponseEntity<AdminExpenseResponseDto> deleteExpense(
            @PathVariable(name = "rawexpenseid") UUID rawExpenseId
    ) {
        adminExpenseService.deleteExpenseById(ExpenseIdWrapper.newOf(rawExpenseId));
        return ResponseEntity.noContent().build();
    }
}
