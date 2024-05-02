package com.example.final_project.api.controllers.admins;

import com.example.final_project.api.requests.budgets.admins.AdminRegisterBudgetRequest;
import com.example.final_project.api.requests.budgets.appusers.PatchBudgetRequest;
import com.example.final_project.api.requests.budgets.appusers.UpdateBudgetRequest;
import com.example.final_project.api.responses.budgets.BudgetResponseDto;
import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.budgets.admins.AdminBudgetService;
import com.example.final_project.domain.budgets.appusers.Budget;
import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
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

import static com.example.final_project.api.controllers.admins.AdminBudgetController.ADMIN_BUDGET_CONTROLLERS_BASE_PATH;

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
                                                                request.description()
        );
        BudgetResponseDto budgetResponseDto = BudgetResponseDto.fromDomain(newBudget);
        return ResponseEntity.created(URI.create("/expenses/" + budgetResponseDto.budgetId().toString()))
                             .body(budgetResponseDto);
    }

    @GetMapping("/{rawbudgetid}")
    ResponseEntity<BudgetResponseDto> getSingleBudget(
            @PathVariable(name = "rawbudgetid") UUID rawBudgetId
    ) {
        Budget budgetById = adminBudgetService.getBudgetById(BudgetIdWrapper.newOf(rawBudgetId));
        return ResponseEntity.ok(BudgetResponseDto.fromDomain(budgetById));
    }

    @GetMapping("/status/{rawbudgetid}")
    ResponseEntity<BudgetStatusDTO> getBudgetStatus(@PathVariable(name = "rawbudgetid") UUID rawBudgetId
    ) {
        return ResponseEntity.ok(
                adminBudgetService.getBudgetStatus(BudgetIdWrapper.newOf(rawBudgetId)));
    }

    @GetMapping("/users/{rawuserid}")
    ResponseEntity<Page<BudgetResponseDto>> getAllBudgetsByUserIdAndPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            @PathVariable(name = "rawuserid") UUID rawUserId
    ) {
        return ResponseEntity.ok(adminBudgetService
                                         .getAllBudgetsByUserIdAndPage(rawUserId, PageRequest.of(
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
                Optional.ofNullable(request.description())
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
                Optional.ofNullable(request.description())
        )));
    }

    @DeleteMapping("/{rawbudgetid}")
    ResponseEntity<BudgetResponseDto> deleteBudget(@PathVariable(name = "rawbudgetid") UUID rawBudgetId
    ) {
        adminBudgetService.deleteBudgetByBudgetId(BudgetIdWrapper.newOf(rawBudgetId));
        return ResponseEntity.noContent().build();
    }
}
