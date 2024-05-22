package com.example.final_project.budget.controller.user;

import com.example.final_project.budget.model.Budget;
import com.example.final_project.budget.model.BudgetIdWrapper;
import com.example.final_project.budget.request.appuser.PatchBudgetRequest;
import com.example.final_project.budget.request.appuser.RegisterBudgetRequest;
import com.example.final_project.budget.request.appuser.UpdateBudgetRequest;
import com.example.final_project.budget.response.BudgetResponseDto;
import com.example.final_project.budget.response.BudgetStatusDTO;
import com.example.final_project.budget.service.user.BudgetService;
import com.nimbusds.jose.util.Pair;
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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.example.final_project.budget.controller.user.BudgetController.BUDGETS_CONTROLLER_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(BUDGETS_CONTROLLER_BASE_PATH)
public class BudgetController {
    static final String BUDGETS_CONTROLLER_BASE_PATH = "/api/budgets";
    private final BudgetService budgetService;

    @PostMapping
    ResponseEntity<EntityModel<BudgetResponseDto>> registerNewBudget(
            @RequestBody @Valid RegisterBudgetRequest request, Authentication authentication
    ) {
        Budget newBudget = budgetService.registerNewBudget(request.title(), request.limit(),
                                                           request.budgetType(), request.maxSingleExpense(),
                                                           request.defaultCurrency(),
                                                           request.budgetStart(), request.budgetEnd(),
                                                           request.description(), authentication
        );
        return ResponseEntity.status(201).body(budgetService.getEntityModel(newBudget));
    }

    @GetMapping("/{budget_uuid}")
    ResponseEntity<EntityModel<BudgetResponseDto>> getSingleBudget(
            @PathVariable(name = "budget_uuid") UUID budgetUUID, Authentication authentication
    ) {
        Budget budget = budgetService.getBudgetById(BudgetIdWrapper.newOf(budgetUUID), authentication);
        return ResponseEntity.ok(budgetService.getEntityModel(budget));
    }

    //TODO implement hateoas
    @GetMapping("/status/{budget_uuid}")
    ResponseEntity<BudgetStatusDTO> getBudgetStatus(@PathVariable(name = "budget_uuid") UUID budgetUUID,
                                                    Authentication authentication
    ) {
        return ResponseEntity.ok(budgetService.getBudgetStatus(BudgetIdWrapper.newOf(budgetUUID), authentication));
    }

    @GetMapping("/statuses")
    ResponseEntity<Page<BudgetStatusDTO>> getBudgetsStatusByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {
        return ResponseEntity.ok(budgetService.getBudgetsStatuses(PageRequest.of(
                page,
                size,
                Sort.by(
                        sortDirection,
                        sortBy
                )
        ), authentication));
    }

    //TODO hateoas
    @GetMapping("/saved_money")
    ResponseEntity<Pair<UUID, BigDecimal>> getAllMoneySaved(Authentication authentication) {
        return ResponseEntity.ok(budgetService.getAllMoneySaved(authentication));
    }

    @GetMapping
    ResponseEntity<PagedModel<BudgetResponseDto>> getBudgetsByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {
        Page<Budget> allByPage = budgetService
                .getAllByPage(PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                sortDirection,
                                sortBy
                        )
                ), authentication);
        return ResponseEntity.ok(budgetService.getEntities(allByPage));
    }

    @PutMapping()
    ResponseEntity<EntityModel<BudgetResponseDto>> updateBudget(
            @Valid @RequestBody UpdateBudgetRequest request,
            Authentication authentication
    ) {
        Budget budget = budgetService.updateBudgetById(
                BudgetIdWrapper.newFromString(request.budgetId()),
                request.title(),
                request.limit(),
                request.budgetType(),
                request.maxSingleExpense(),
                request.defaultCurrency(),
                request.budgetStart(),
                request.budgetEnd(),
                request.description(),
                authentication
        );
        return ResponseEntity.ok(budgetService.getEntityModel(budget));
    }

    @PatchMapping()
    ResponseEntity<EntityModel<BudgetResponseDto>> patchBudget(
            @Valid @RequestBody PatchBudgetRequest request,
            Authentication authentication
    ) {
        Budget budget = budgetService.patchBudgetContent(
                BudgetIdWrapper.newFromString(request.budgetId()),
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.limit()),
                Optional.ofNullable(request.budgetType()),
                Optional.ofNullable(request.maxSingleExpense()),
                Optional.ofNullable(request.defaultCurrency()),
                Optional.ofNullable(request.budgetStart()),
                Optional.ofNullable(request.budgetEnd()),
                Optional.ofNullable(request.description()),
                authentication
        );
        return ResponseEntity.ok(budgetService.getEntityModel(budget));
    }

    @DeleteMapping("/{budget_uuid}")
    ResponseEntity<EntityModel<BudgetResponseDto>> deleteBudget(@PathVariable(name = "budget_uuid") UUID budgetUUID,
                                                                Authentication authentication
    ) {
        budgetService.deleteAllBudgetExpensesByBudgetId(BudgetIdWrapper.newOf(budgetUUID), authentication);
        return ResponseEntity.noContent().build();
    }
}
