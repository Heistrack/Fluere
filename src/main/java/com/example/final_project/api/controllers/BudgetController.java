package com.example.final_project.api.controllers;
import com.example.final_project.api.requests.budgets.RegisterBudgetRequest;
import com.example.final_project.api.requests.budgets.UpdateBudgetRequest;
import com.example.final_project.api.responses.BudgetResponseDto;
import com.example.final_project.domain.budgets.Budget;
import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.budgets.BudgetService;
import com.example.final_project.domain.budgets.TypeOfBudget;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

import static com.example.final_project.api.controllers.BudgetController.BUDGETS_BASE_PATH;

@RestController
@RequestMapping(BUDGETS_BASE_PATH)
public class BudgetController {

    static final String BUDGETS_BASE_PATH = "/budgets";
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/{rawBudgetId}")
    ResponseEntity<BudgetResponseDto> getSingleBudget(
            @PathVariable String rawBudgetId
    ) {
        Optional<Budget> budgetById = budgetService.getBudgetById(new BudgetId(rawBudgetId));
        return ResponseEntity.of(budgetById.map(BudgetResponseDto::fromDomain));
    }

    @GetMapping
    ResponseEntity<List<BudgetResponseDto>> getAllBudgets() {

        List<Budget> budgets = budgetService.getBudgets();
        return ResponseEntity.ok(budgets.stream().map(BudgetResponseDto::fromDomain).toList());
    }

    @PostMapping
    ResponseEntity<BudgetResponseDto> registerNewBudget(
            @RequestBody @Valid RegisterBudgetRequest request
    ) {
        Budget newBudget = budgetService.registerNewBudget(request.title(), request.limit(),
                request.typeOfBudget(), request.maxSingleExpense());
        BudgetResponseDto budgetResponseDto = BudgetResponseDto.fromDomain(newBudget);
        return ResponseEntity.created(URI.create("/expenses/" + budgetResponseDto.budgetId())).body(budgetResponseDto);
    }

    @DeleteMapping("/{rawBudgetId}")
    public ResponseEntity<BudgetResponseDto> deleteBudget(@PathVariable String rawBudgetId) {
        budgetService.getBudgetById(new BudgetId(rawBudgetId))
                .ifPresent(budget -> budgetService.deleteBudgetById(budget.budgetId()));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{rawBudgetId}")
    public ResponseEntity<BudgetResponseDto> updateBudget(@PathVariable UUID rawBudgetId, @RequestBody RegisterBudgetRequest request) {
        Budget updatedBudget = budgetService.updateBudgetById(new BudgetId(
                        rawBudgetId.toString()),
                request.title(),
                request.limit(),
                request.typeOfBudget(),
                request.maxSingleExpense());
        return ResponseEntity.ok(BudgetResponseDto.fromDomain(updatedBudget));
    }

    @PatchMapping("/{rawBudgetId}")
    public ResponseEntity<BudgetResponseDto> updateBudgetField(@PathVariable UUID rawBudgetId,
                                                               @RequestBody UpdateBudgetRequest request) {

        Optional<String> title = Optional.ofNullable(request.title());
        Optional<BigDecimal> limit = Optional.ofNullable(request.limit());
        Optional<TypeOfBudget> typeOfBudget = Optional.ofNullable(request.typeOfBudget());
        Optional<BigDecimal> maxSingleExpense = Optional.ofNullable(request.maxSingleExpense());

        return ResponseEntity.of(budgetService.updateBudgetContent(new BudgetId(rawBudgetId.toString()), title,
                limit,
                typeOfBudget,
                maxSingleExpense)
                .map(BudgetResponseDto::fromDomain));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
