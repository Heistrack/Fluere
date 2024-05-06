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
    ResponseEntity<BudgetResponseDto> registerNewBudget(
            @RequestBody @Valid AdminRegisterBudgetRequest request
    ) {
        Budget newBudget = adminBudgetService.registerNewBudget(UserIdWrapper.newFromString(request.userId()),
                                                                request.title(), request.limit(),
                                                                request.budgetType(), request.maxSingleExpense(),
                                                                request.defaultCurrency(),
                                                                request.budgetStart(), request.budgetEnd(),
                                                                request.description()
        );

        BudgetResponseDto budgetResponseDto = BudgetResponseDto.fromDomain(newBudget);
        return ResponseEntity.created(URI.create("/expenses/" + budgetResponseDto.budgetId().toString()))
                             .body(budgetResponseDto);
    }

    @GetMapping("/{budget_uuid}")
    ResponseEntity<BudgetResponseDto> getSingleBudget(
            @PathVariable(name = "budget_uuid") UUID budgetUUID
    ) {
        Budget budgetById = adminBudgetService.getBudgetById(BudgetIdWrapper.newOf(budgetUUID));
        return ResponseEntity.ok(BudgetResponseDto.fromDomain(budgetById));
    }

    @GetMapping("/status/{budget_uuid}")
    ResponseEntity<BudgetStatusDTO> getBudgetStatus(@PathVariable(name = "budget_uuid") UUID budgetUUID
    ) {
        return ResponseEntity.ok(
                adminBudgetService.getBudgetStatus(BudgetIdWrapper.newOf(budgetUUID)));
    }

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
    ResponseEntity<Page<BudgetResponseDto>> getAllBudgetsByUserIdAndPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            @PathVariable(name = "user_uuid") UUID userUUID
    ) {
        return ResponseEntity.ok(adminBudgetService
                                         .getAllBudgetsByUserIdAndPage(userUUID, PageRequest.of(
                                                 page,
                                                 size,
                                                 Sort.by(
                                                         sortDirection,
                                                         sortBy
                                                 )
                                         ))
                                         .map(BudgetResponseDto::fromDomain));
    }

    @GetMapping("/users")
    ResponseEntity<Page<BudgetResponseDto>> getAllBudgetsByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        return ResponseEntity.ok(adminBudgetService
                                         .getAllBudgetsByPage(PageRequest.of(
                                                 page,
                                                 size,
                                                 Sort.by(
                                                         sortDirection,
                                                         sortBy
                                                 )
                                         ))
                                         .map(BudgetResponseDto::fromDomain));
    }

    @PutMapping()
    ResponseEntity<BudgetResponseDto> updateBudget(
            @Valid @RequestBody UpdateBudgetRequest request
    ) {
        return ResponseEntity.ok(BudgetResponseDto.fromDomain(adminBudgetService.updateBudgetById(
                BudgetIdWrapper.newFromString(request.budgetId()),
                request.title(),
                request.limit(),
                request.budgetType(),
                request.maxSingleExpense(),
                request.defaultCurrency(),
                request.budgetStart(),
                request.budgetEnd(),
                request.description()
        )));
    }

    @PatchMapping()
    ResponseEntity<BudgetResponseDto> patchBudget(
            @Valid @RequestBody PatchBudgetRequest request
    ) {
        return ResponseEntity.ok(BudgetResponseDto.fromDomain(adminBudgetService.patchBudgetContent(
                BudgetIdWrapper.newFromString(request.budgetId()),
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.limit()),
                Optional.ofNullable(request.budgetType()),
                Optional.ofNullable(request.maxSingleExpense()),
                Optional.ofNullable(request.defaultCurrency()),
                Optional.ofNullable(request.budgetStart()),
                Optional.ofNullable(request.budgetEnd()),
                Optional.ofNullable(request.description())
        )));
    }

    @DeleteMapping("/{budget_uuid}")
    ResponseEntity<BudgetResponseDto> deleteBudget(@PathVariable(name = "budget_uuid") UUID budgetUUID
    ) {
        adminBudgetService.deleteBudgetByBudgetId(BudgetIdWrapper.newOf(budgetUUID));
        return ResponseEntity.noContent().build();
    }
}
