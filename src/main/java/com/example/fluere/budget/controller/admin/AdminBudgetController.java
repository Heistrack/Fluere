package com.example.fluere.budget.controller.admin;

import com.example.fluere.budget.model.Budget;
import com.example.fluere.budget.model.BudgetIdWrapper;
import com.example.fluere.budget.request.admin.AdminRegisterBudgetRequest;
import com.example.fluere.budget.request.appuser.PatchBudgetRequest;
import com.example.fluere.budget.request.appuser.UpdateBudgetRequest;
import com.example.fluere.budget.response.BudgetResponseDTO;
import com.example.fluere.budget.response.BudgetStatusDTO;
import com.example.fluere.budget.response.BudgetUserMoneySavedDTO;
import com.example.fluere.budget.service.admin.AdminBudgetService;
import com.example.fluere.userentity.model.UserIdWrapper;
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

import static com.example.fluere.budget.controller.admin.AdminBudgetController.ADMIN_BUDGET_CONTROLLERS_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(ADMIN_BUDGET_CONTROLLERS_BASE_PATH)
public class AdminBudgetController {
    static final String ADMIN_BUDGET_CONTROLLERS_BASE_PATH = "/api/x/budgets";
    private final AdminBudgetService adminBudgetService;

    @PostMapping
    ResponseEntity<EntityModel<BudgetResponseDTO>> registerNewBudget(
            @RequestBody @Valid AdminRegisterBudgetRequest request
    ) {
        Budget newBudget = adminBudgetService.registerNewBudget(UserIdWrapper.newFromString(request.userId()),
                                                                request.title(), request.limit(),
                                                                request.budgetType(), request.maxSingleExpense(),
                                                                request.defaultCurrency(),
                                                                request.budgetStart(), request.budgetEnd(),
                                                                request.description()
        );
        return ResponseEntity.status(201).body(adminBudgetService.getEntityModel(
                BudgetResponseDTO.fromDomain(newBudget),
                BudgetResponseDTO.class
        ));
    }

    @GetMapping("/{budget_uuid}")
    ResponseEntity<EntityModel<BudgetResponseDTO>> getSingleBudget(
            @PathVariable(name = "budget_uuid") UUID budgetUUID
    ) {
        Budget budget = adminBudgetService.getBudgetById(BudgetIdWrapper.newOf(budgetUUID));
        return ResponseEntity.ok(adminBudgetService.getEntityModel(
                BudgetResponseDTO.fromDomain(budget),
                BudgetResponseDTO.class
        ));
    }

    @GetMapping("/status/{budget_uuid}")
    ResponseEntity<EntityModel<BudgetStatusDTO>> getBudgetStatus(@PathVariable(name = "budget_uuid") UUID budgetUUID
    ) {
        BudgetStatusDTO budgetStatus = adminBudgetService.getBudgetStatus(BudgetIdWrapper.newOf(budgetUUID));
        return ResponseEntity.ok(adminBudgetService.getEntityModel(budgetStatus, BudgetStatusDTO.class));
    }

    @GetMapping("/statuses/{user_uuid}")
    ResponseEntity<PagedModel<BudgetStatusDTO>> getBudgetsStatusByPage(
            @PathVariable(name = "user_uuid") UUID userUUID,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        Page<BudgetStatusDTO> budgetsStatuses = adminBudgetService.getBudgetsStatuses(PageRequest.of(
                page,
                size,
                Sort.by(
                        sortDirection,
                        sortBy
                )
        ), UserIdWrapper.newOf(userUUID));
        return ResponseEntity.ok(adminBudgetService.getEntities(budgetsStatuses, BudgetStatusDTO.class));
    }

    @GetMapping("/saved_money/{user_uuid}")
    ResponseEntity<EntityModel<BudgetUserMoneySavedDTO>> getAllMoneySavedByUser(
            @PathVariable(name = "user_uuid") UUID userUUID
    ) {
        BudgetUserMoneySavedDTO allMoneySaved = adminBudgetService.getAllMoneySavedByUser(userUUID);
        return ResponseEntity.ok(adminBudgetService.getEntityModel(allMoneySaved, BudgetUserMoneySavedDTO.class));
    }

    @GetMapping("/users/{user_uuid}")
    ResponseEntity<PagedModel<BudgetResponseDTO>> getAllBudgetsByUserIdAndPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            @PathVariable(name = "user_uuid") UUID userUUID
    ) {
        Page<BudgetResponseDTO> budgetResponseDtos = adminBudgetService
                .getAllBudgetsByUserIdAndPage(userUUID, PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                sortDirection,
                                sortBy
                        )
                )).map(BudgetResponseDTO::fromDomain);
        return ResponseEntity.ok(adminBudgetService.getEntities(budgetResponseDtos, BudgetResponseDTO.class));
    }

    @GetMapping("/users")
    ResponseEntity<PagedModel<BudgetResponseDTO>> getAllBudgetsByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        Page<BudgetResponseDTO> budgetResponseDtos = adminBudgetService
                .getAllBudgetsByPage(PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                sortDirection,
                                sortBy
                        )
                )).map(BudgetResponseDTO::fromDomain);
        return ResponseEntity.ok(adminBudgetService.getEntities(budgetResponseDtos, BudgetResponseDTO.class));
    }

    @PutMapping()
    ResponseEntity<EntityModel<BudgetResponseDTO>> updateBudget(
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
        return ResponseEntity.ok(
                adminBudgetService.getEntityModel(BudgetResponseDTO.fromDomain(budget), BudgetResponseDTO.class));
    }

    @PatchMapping()
    ResponseEntity<EntityModel<BudgetResponseDTO>> patchBudget(
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
        return ResponseEntity.ok(
                adminBudgetService.getEntityModel(BudgetResponseDTO.fromDomain(budget), BudgetResponseDTO.class));
    }

    @DeleteMapping("/{budget_uuid}")
    ResponseEntity<EntityModel<BudgetResponseDTO>> deleteBudget(@PathVariable(name = "budget_uuid") UUID budgetUUID
    ) {
        adminBudgetService.deleteBudgetByBudgetId(BudgetIdWrapper.newOf(budgetUUID));
        return ResponseEntity.noContent().build();
    }
}
