package com.example.final_project.api.controllers.users;

import com.example.final_project.api.requests.budgets.appusers.PatchBudgetRequest;
import com.example.final_project.api.requests.budgets.appusers.RegisterBudgetRequest;
import com.example.final_project.api.requests.budgets.appusers.UpdateBudgetRequest;
import com.example.final_project.api.responses.budgets.BudgetResponseDto;
import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.budgets.appusers.Budget;
import com.example.final_project.domain.budgets.appusers.BudgetIdWrapper;
import com.example.final_project.domain.budgets.appusers.BudgetService;
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

import static com.example.final_project.api.controllers.users.BudgetController.BUDGETS_CONTROLLER_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(BUDGETS_CONTROLLER_BASE_PATH)
public class BudgetController {
    static final String BUDGETS_CONTROLLER_BASE_PATH = "/api/budgets";
    private final BudgetService budgetService;

    @PostMapping
    ResponseEntity<BudgetResponseDto> registerNewBudget(
            @RequestBody @Valid RegisterBudgetRequest request, Authentication authentication
    ) {
        Budget newBudget = budgetService.registerNewBudget(request.title(), request.limit(),
                                                           request.budgetType(), request.maxSingleExpense(),
                                                           request.description(),
                                                           authentication
        );

        BudgetResponseDto budgetResponseDto = BudgetResponseDto.fromDomain(newBudget);
        return ResponseEntity.created(URI.create("/expenses/" + budgetResponseDto.budgetId().toString()))
                             .body(budgetResponseDto);
    }

    @GetMapping("/{rawbudgetid}")
    ResponseEntity<BudgetResponseDto> getSingleBudget(
            @PathVariable(name = "rawbudgetid") UUID rawBudgetId, Authentication authentication
    ) {
        Budget budgetById = budgetService.getBudgetById(BudgetIdWrapper.newOf(rawBudgetId), authentication);
        return ResponseEntity.ok(BudgetResponseDto.fromDomain(budgetById));
    }

    @GetMapping("/status/{rawbudgetid}")
    ResponseEntity<BudgetStatusDTO> getBudgetStatus(@PathVariable(name = "rawbudgetid") UUID rawBudgetId,
                                                    Authentication authentication
    ) {
        return ResponseEntity.ok(budgetService.getBudgetStatus(BudgetIdWrapper.newOf(rawBudgetId), authentication));
    }

    @GetMapping
    ResponseEntity<Page<BudgetResponseDto>> getBudgetsByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {
        return ResponseEntity.ok(budgetService
                                         .getAllByPage(authentication, PageRequest.of(
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
            @Valid @RequestBody UpdateBudgetRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(BudgetResponseDto.fromDomain(budgetService.updateBudgetById(
                BudgetIdWrapper.newFromString(request.budgetId()),
                request.title(),
                request.limit(),
                request.budgetType(),
                request.maxSingleExpense(),
                Optional.ofNullable(request.description()),
                authentication
        )));
    }

    @PatchMapping()
    ResponseEntity<BudgetResponseDto> patchBudget(
            @Valid @RequestBody PatchBudgetRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(BudgetResponseDto.fromDomain(budgetService.patchBudgetContent(
                BudgetIdWrapper.newFromString(request.budgetId()),
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.limit()),
                Optional.ofNullable(request.budgetType()),
                Optional.ofNullable(request.maxSingleExpense()),
                Optional.ofNullable(request.description()),
                authentication
        )));
    }

    @DeleteMapping("/{rawbudgetid}")
    ResponseEntity<BudgetResponseDto> deleteBudget(@PathVariable(name = "rawbudgetid") UUID rawBudgetId,
                                                   Authentication authentication
    ) {
        budgetService.deleteAllBudgetExpensesByBudgetId(BudgetIdWrapper.newOf(rawBudgetId), authentication);
        return ResponseEntity.noContent().build();
    }
}
