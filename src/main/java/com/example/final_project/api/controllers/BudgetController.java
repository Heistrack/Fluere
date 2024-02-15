package com.example.final_project.api.controllers;

import com.example.final_project.api.requests.budgets.RegisterBudgetRequest;
import com.example.final_project.api.requests.budgets.UpdateBudgetRequest;
import com.example.final_project.api.responses.budgets.BudgetResponseDto;
import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetIdWrapper;
import com.example.final_project.domain.budgets.BudgetService;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.users.UserIdWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.example.final_project.api.controllers.BudgetController.BUDGETS_CONTROLLER_BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(BUDGETS_CONTROLLER_BASE_PATH)
public class BudgetController {
    static final String BUDGETS_CONTROLLER_BASE_PATH = "/budgets";
    private final BudgetService budgetService;
    private final JwtService jwtService;

    @GetMapping("/{rawbudgetid}")
    ResponseEntity<BudgetResponseDto> getSingleBudget(
            @PathVariable(name = "rawbudgetid") UUID rawBudgetId, Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Budget budgetById = budgetService.getBudgetById(BudgetIdWrapper.newOf(rawBudgetId), userId);
        return ResponseEntity.ok(BudgetResponseDto.fromDomain(budgetById));
    }

    @DeleteMapping("/{rawbudgetid}")
    public ResponseEntity<BudgetResponseDto> deleteBudget(@PathVariable(name = "rawbudgetid") UUID rawBudgetId,
                                                          Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);

        Budget currentBudget = budgetService.getBudgetById(BudgetIdWrapper.newOf(rawBudgetId), userId);
        budgetService.deleteBudgetById(currentBudget.budgetId(), userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{rawbudgetid}")
    ResponseEntity<BudgetStatusDTO> getBudgetStatus(@PathVariable(name = "rawbudgetid") UUID rawBudgetId,
                                                    Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);

        return ResponseEntity.ok(budgetService.getBudgetStatus(BudgetIdWrapper.newOf(rawBudgetId), userId));
    }

    @PostMapping
    ResponseEntity<BudgetResponseDto> registerNewBudget(
            @RequestBody @Valid RegisterBudgetRequest request, Authentication authentication
    ) {

        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);

        Budget newBudget = budgetService.registerNewBudget(request.title(), request.limit(),
                                                           request.typeOfBudget(), request.maxSingleExpense(),
                                                           userId
        );
        BudgetResponseDto budgetResponseDto = BudgetResponseDto.fromDomain(newBudget);
        return ResponseEntity.created(URI.create("/expenses/" + budgetResponseDto.budgetId().toString()))
                             .body(budgetResponseDto);
    }

    @PutMapping("/{rawbudgetid}")
    public ResponseEntity<BudgetResponseDto> updateBudget(@PathVariable(name = "rawbudgetid") UUID rawBudgetId,
                                                          @Valid @RequestBody RegisterBudgetRequest request,
                                                          Authentication authentication
    ) {
        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);

        Budget updatedBudget = budgetService.updateBudgetById(
                BudgetIdWrapper.newOf(rawBudgetId),
                request.title(),
                request.limit(),
                request.typeOfBudget(),
                request.maxSingleExpense(),
                userId,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(BudgetResponseDto.fromDomain(updatedBudget));
    }

    @PatchMapping("/{rawbudgetid}")
    public ResponseEntity<BudgetResponseDto> patchBudget(@PathVariable(name = "rawbudgetid") UUID rawBudgetId,
                                                         @Valid @RequestBody UpdateBudgetRequest request,
                                                         Authentication authentication
    ) {

        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);

        Budget patchedBudget = budgetService.patchBudgetContent(
                BudgetIdWrapper.newOf(rawBudgetId),
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.limit()),
                Optional.ofNullable(request.typeOfBudget()),
                Optional.ofNullable(request.maxSingleExpense()),
                userId
        );

        return ResponseEntity.ok(BudgetResponseDto.fromDomain(patchedBudget));
    }

    @GetMapping
    ResponseEntity<Page<BudgetResponseDto>> getBudgetByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {

        UserIdWrapper userId = jwtService.extractUserIdFromRequestAuth(authentication);

        return ResponseEntity.ok(budgetService.findAllByPage(
                                                      userId,
                                                      PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
                                              )
                                              .map(BudgetResponseDto::fromDomain));
    }
}
