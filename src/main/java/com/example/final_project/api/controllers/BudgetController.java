package com.example.final_project.api.controllers;

import com.example.final_project.api.requests.budgets.RegisterBudgetRequest;
import com.example.final_project.api.requests.budgets.UpdateBudgetRequest;
import com.example.final_project.api.responses.budgets.BudgetResponseDto;
import com.example.final_project.api.responses.budgets.BudgetStatusDTO;
import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.budgets.BudgetService;
import com.example.final_project.domain.budgets.TypeOfBudget;
import com.example.final_project.domain.securities.jwt.JwtService;
import com.example.final_project.domain.users.UserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @GetMapping("/{rawBudgetId}")
    ResponseEntity<BudgetResponseDto> getSingleBudget(
            @PathVariable String rawBudgetId, Authentication authentication
    ) {
        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);
        Optional<Budget> budgetById = budgetService.getBudgetById(BudgetId.newFromString(rawBudgetId), userId);
        return ResponseEntity.of(budgetById.map(BudgetResponseDto::fromDomain));
    }

    @GetMapping
    ResponseEntity<Page<BudgetResponseDto>> getBudgetByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "budgetId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
            Authentication authentication
    ) {

        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);

        return ResponseEntity.ok(budgetService.findAllByPage(
                                                      userId,
                                                      PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
                                              )
                                              .map(BudgetResponseDto::fromDomain));
    }

    @GetMapping("/{rawBudgetId}/status")
    ResponseEntity<BudgetStatusDTO> getBudgetStatus(@PathVariable String rawBudgetId,
                                                    Authentication authentication
    ) {
        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);

        return ResponseEntity.status(HttpStatus.OK).body(
                budgetService.getBudgetStatus(BudgetId.newFromString(rawBudgetId), userId)
        );
    }

    @PostMapping
    ResponseEntity<BudgetResponseDto> registerNewBudget(
            @RequestBody @Valid RegisterBudgetRequest request, Authentication authentication
    ) {

        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);

        Budget newBudget = budgetService.registerNewBudget(request.title(), request.limit(),
                                                           request.typeOfBudget(), request.maxSingleExpense(), userId
        );
        BudgetResponseDto budgetResponseDto = BudgetResponseDto.fromDomain(newBudget);
        return ResponseEntity.created(URI.create("/expenses/" + budgetResponseDto.budgetId())).body(budgetResponseDto);
    }

    @DeleteMapping("/{rawBudgetId}")
    public ResponseEntity<BudgetResponseDto> deleteBudget(@PathVariable String rawBudgetId,
                                                          Authentication authentication
    ) {
        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);

        budgetService.getBudgetById(BudgetId.newFromString(rawBudgetId), userId)
                     .ifPresent(budget -> budgetService.deleteBudgetById(budget.budgetId(), userId));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{rawBudgetId}")
    public ResponseEntity<BudgetResponseDto> updateBudget(@PathVariable UUID rawBudgetId,
                                                          @RequestBody RegisterBudgetRequest request,
                                                          Authentication authentication
    ) {
        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);

        Budget updatedBudget = budgetService.updateBudgetById(
                BudgetId.newOf(rawBudgetId),
                request.title(),
                request.limit(),
                request.typeOfBudget(),
                request.maxSingleExpense(),
                userId,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(BudgetResponseDto.fromDomain(updatedBudget));
    }

    @PatchMapping("/{rawBudgetId}")
    public ResponseEntity<BudgetResponseDto> updateBudgetField(@PathVariable String rawBudgetId,
                                                               @RequestBody UpdateBudgetRequest request,
                                                               Authentication authentication
    ) {

        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);

        Optional<String> title = Optional.ofNullable(request.title());
        Optional<BigDecimal> limit = Optional.ofNullable(request.limit());
        Optional<TypeOfBudget> typeOfBudget = Optional.ofNullable(request.typeOfBudget());
        Optional<BigDecimal> maxSingleExpense = Optional.ofNullable(request.maxSingleExpense());
        Optional<LocalDateTime> timestamp = Optional.empty();

        return ResponseEntity.of(budgetService.updateBudgetContent(BudgetId.newFromString(rawBudgetId), title,
                                                                   limit,
                                                                   typeOfBudget,
                                                                   maxSingleExpense,
                                                                   userId,
                                                                   timestamp
                                              )
                                              .map(BudgetResponseDto::fromDomain));
    }

    @GetMapping("/{budgetId}/status")
    ResponseEntity<BudgetStatusDTO> getSingleBudgetStatus(
            @PathVariable String budgetId,
            Authentication authentication
    ) {
        UserId userId = jwtService.extractUserIdFromRequestAuth(authentication);
        return ResponseEntity.of(Optional.ofNullable(budgetService.getBudgetStatus(BudgetId.newFromString(budgetId), userId)));
    }
}
