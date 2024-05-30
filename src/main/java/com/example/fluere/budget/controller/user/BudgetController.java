package com.example.fluere.budget.controller.user;

import com.example.fluere.budget.model.Budget;
import com.example.fluere.budget.model.BudgetIdWrapper;
import com.example.fluere.budget.request.appuser.PatchBudgetRequest;
import com.example.fluere.budget.request.appuser.RegisterBudgetRequest;
import com.example.fluere.budget.request.appuser.UpdateBudgetRequest;
import com.example.fluere.budget.response.BudgetResponseDTO;
import com.example.fluere.budget.response.BudgetStatusDTO;
import com.example.fluere.budget.response.BudgetUserMoneySavedDTO;
import com.example.fluere.budget.service.user.BudgetService;
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

import static com.example.fluere.budget.controller.user.BudgetController.BUDGETS_CONTROLLER_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(BUDGETS_CONTROLLER_BASE_PATH)
public class BudgetController {
    static final String BUDGETS_CONTROLLER_BASE_PATH = "/api/budgets";
    private final BudgetService budgetService;

    @PostMapping
    ResponseEntity<EntityModel<BudgetResponseDTO>> registerNewBudget(
            @RequestBody @Valid RegisterBudgetRequest request, Authentication authentication
    ) {
        Budget newBudget = budgetService.registerNewBudget(request.title(), request.limit(),
                                                           request.budgetType(), request.maxSingleExpense(),
                                                           request.defaultCurrency(),
                                                           request.budgetStart(), request.budgetEnd(),
                                                           request.description(), authentication
        );
        return ResponseEntity.status(201).body(budgetService.getEntityModel(
                BudgetResponseDTO.fromDomain(newBudget),
                BudgetResponseDTO.class
        ));
    }

    @GetMapping("/{budget_uuid}")
    ResponseEntity<EntityModel<BudgetResponseDTO>> getSingleBudget(
            @PathVariable(name = "budget_uuid") UUID budgetUUID, Authentication authentication
    ) {
        Budget budget = budgetService.getBudgetById(BudgetIdWrapper.newOf(budgetUUID), authentication);
        return ResponseEntity.ok(
                budgetService.getEntityModel(BudgetResponseDTO.fromDomain(budget), BudgetResponseDTO.class));
    }

    @GetMapping("/status/{budget_uuid}")
    ResponseEntity<EntityModel<BudgetStatusDTO>> getBudgetStatus(@PathVariable(name = "budget_uuid") UUID budgetUUID,
                                                                 Authentication authentication
    ) {
        BudgetStatusDTO budgetStatus = budgetService.getBudgetStatus(BudgetIdWrapper.newOf(budgetUUID), authentication);
        return ResponseEntity.ok(budgetService.getEntityModel(budgetStatus, BudgetStatusDTO.class));
    }

    @GetMapping("/statuses")
    ResponseEntity<PagedModel<BudgetStatusDTO>> getBudgetsStatusByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {
        Page<BudgetStatusDTO> budgetsStatuses = budgetService.getBudgetsStatuses(PageRequest.of(
                page,
                size,
                Sort.by(
                        sortDirection,
                        sortBy
                )
        ), authentication);
        return ResponseEntity.ok(budgetService.getEntities(budgetsStatuses, BudgetStatusDTO.class));
    }

    @GetMapping("/saved_money")
    ResponseEntity<EntityModel<BudgetUserMoneySavedDTO>> getUserSavedMoney(Authentication authentication) {
        BudgetUserMoneySavedDTO allMoneySaved = budgetService.getAllMoneySavedByUser(authentication);
        return ResponseEntity.ok(budgetService.getEntityModel(allMoneySaved, BudgetUserMoneySavedDTO.class));
    }

    @GetMapping("/total_saved_money")
    ResponseEntity<EntityModel<BudgetUserMoneySavedDTO>> getAllMoneySaved() {
        BudgetUserMoneySavedDTO allMoneySaved = budgetService.getAllMoneySaved();
        return ResponseEntity.ok(budgetService.getEntityModel(allMoneySaved, BudgetUserMoneySavedDTO.class));
    }

    @GetMapping
    ResponseEntity<PagedModel<BudgetResponseDTO>> getBudgetsByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {
        Page<BudgetResponseDTO> allByPage = budgetService
                .getAllByPage(PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                sortDirection,
                                sortBy
                        )
                ), authentication).map(BudgetResponseDTO::fromDomain);
        return ResponseEntity.ok(budgetService.getEntities(allByPage, BudgetResponseDTO.class));
    }

    @PutMapping()
    ResponseEntity<EntityModel<BudgetResponseDTO>> updateBudget(
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
        return ResponseEntity.ok(
                budgetService.getEntityModel(BudgetResponseDTO.fromDomain(budget), BudgetResponseDTO.class));
    }

    @PatchMapping()
    ResponseEntity<EntityModel<BudgetResponseDTO>> patchBudget(
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
        return ResponseEntity.ok(
                budgetService.getEntityModel(BudgetResponseDTO.fromDomain(budget), BudgetResponseDTO.class));
    }

    @DeleteMapping("/{budget_uuid}")
    ResponseEntity<EntityModel<BudgetResponseDTO>> deleteBudget(@PathVariable(name = "budget_uuid") UUID budgetUUID,
                                                                Authentication authentication
    ) {
        budgetService.deleteAllBudgetExpensesByBudgetId(BudgetIdWrapper.newOf(budgetUUID), authentication);
        return ResponseEntity.noContent().build();
    }
}
