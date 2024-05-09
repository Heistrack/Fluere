package com.example.final_project.budget.controller.admin;

import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.budget.request.admin.AdminRegisterBudgetRequest;
import com.example.final_project.budget.request.appuser.PatchBudgetRequest;
import com.example.final_project.budget.request.appuser.UpdateBudgetRequest;
import com.example.final_project.budget.response.BudgetResponseDto;
import com.example.final_project.budget.response.BudgetStatusDTO;
import com.example.final_project.budget.service.admin.AdminBudgetService;
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

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static com.example.final_project.budget.controller.admin.AdminBudgetController.ADMIN_BUDGET_CONTROLLERS_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(ADMIN_BUDGET_CONTROLLERS_BASE_PATH)
public class AdminBudgetController {
    static final String ADMIN_BUDGET_CONTROLLERS_BASE_PATH = "/api/x/budgets";
    private final AdminBudgetService adminBudgetService;

    @PostMapping
    ResponseEntity<EntityModel<BudgetResponseDto>> registerNewBudget(
            @RequestBody @Valid AdminRegisterBudgetRequest request
    ) {
        Budget newBudget = adminBudgetService.registerNewBudget(UserIdWrapper.newFromString(request.userId()),
                                                                request.title(), request.limit(),
                                                                request.budgetType(), request.maxSingleExpense(),
                                                                request.defaultCurrency(),
                                                                request.budgetStart(), request.budgetEnd(),
                                                                request.description()
        );
        return ResponseEntity.status(201).body(adminBudgetService.getEntityModel(newBudget));
    }

    @GetMapping("/{budget_uuid}")
    ResponseEntity<EntityModel<BudgetResponseDto>> getSingleBudget(
            @PathVariable(name = "budget_uuid") UUID budgetUUID
    ) {
        Budget budget = adminBudgetService.getBudgetById(BudgetIdWrapper.newOf(budgetUUID));
        return ResponseEntity.ok(adminBudgetService.getEntityModel(budget));
    }

//TODO hateoas for two endpoints
    @GetMapping("/status/{budget_uuid}")
    ResponseEntity<BudgetStatusDTO> getBudgetStatus(@PathVariable(name = "budget_uuid") UUID budgetUUID
    ) {
        return ResponseEntity.ok(
                adminBudgetService.getBudgetStatus(BudgetIdWrapper.newOf(budgetUUID)));
    }
//TODO add HATEOAS
    @GetMapping("/statuses/{user_uuid}")
    ResponseEntity<Page<BudgetStatusDTO>> getBudgetsStatusByPage(
            @PathVariable(name = "user_uuid") UUID userUUID,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        return ResponseEntity.ok(adminBudgetService.getBudgetsStatuses(PageRequest.of(
                page,
                size,
                Sort.by(
                        sortDirection,
                        sortBy
                )
        ), UserIdWrapper.newOf(userUUID)));
    }

    @GetMapping("/users/{user_uuid}")
    ResponseEntity<PagedModel<BudgetResponseDto>> getAllBudgetsByUserIdAndPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            @PathVariable(name = "user_uuid") UUID userUUID
    ) {
        Page<Budget> budgets = adminBudgetService
                .getAllBudgetsByUserIdAndPage(userUUID, PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                sortDirection,
                                sortBy
                        )
                ));
        return ResponseEntity.ok(adminBudgetService.getEntities(budgets));
    }

    @GetMapping("/users")
    ResponseEntity<PagedModel<BudgetResponseDto>> getAllBudgetsByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        Page<Budget> budgets = adminBudgetService
                .getAllBudgetsByPage(PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                sortDirection,
                                sortBy
                        )
                ));
        return ResponseEntity.ok(adminBudgetService.getEntities(budgets));
    }

    @PutMapping()
    ResponseEntity<EntityModel<BudgetResponseDto>> updateBudget(
            @Valid @RequestBody UpdateBudgetRequest request
    ) {
        Budget budget = adminBudgetService.updateBudgetById(
                BudgetIdWrapper.newFromString(request.budgetId()),
                request.title(),
                request.limit(),
                request.budgetType(),
                request.maxSingleExpense(),
                request.defaultCurrency(),
                request.budgetStart(),
                request.budgetEnd(),
                request.description()
        );
        return ResponseEntity.ok(adminBudgetService.getEntityModel(budget));
    }

    @PatchMapping()
    ResponseEntity<EntityModel<BudgetResponseDto>> patchBudget(
            @Valid @RequestBody PatchBudgetRequest request
    ) {
        Budget budget = adminBudgetService.patchBudgetContent(
                BudgetIdWrapper.newFromString(request.budgetId()),
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.limit()),
                Optional.ofNullable(request.budgetType()),
                Optional.ofNullable(request.maxSingleExpense()),
                Optional.ofNullable(request.defaultCurrency()),
                Optional.ofNullable(request.budgetStart()),
                Optional.ofNullable(request.budgetEnd()),
                Optional.ofNullable(request.description())
        );
        return ResponseEntity.ok(adminBudgetService.getEntityModel(budget));
    }

    @DeleteMapping("/{budget_uuid}")
    ResponseEntity<EntityModel<BudgetResponseDto>> deleteBudget(@PathVariable(name = "budget_uuid") UUID budgetUUID
    ) {
        adminBudgetService.deleteBudgetByBudgetId(BudgetIdWrapper.newOf(budgetUUID));
        return ResponseEntity.noContent().build();
    }
}
