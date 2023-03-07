package com.example.final_project.api.controllers;

import com.example.final_project.api.requests.expenses.RegisterExpenseRequest;
import com.example.final_project.api.requests.expenses.UpdateExpenseRequest;
import com.example.final_project.api.responses.ExpenseResponseDto;
import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseId;
import com.example.final_project.domain.expenses.ExpensesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort.Direction;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

import static com.example.final_project.api.controllers.ExpensesController.EXPENSES_BASE_PATH;

@RestController
@RequestMapping(EXPENSES_BASE_PATH)
public class ExpensesController {
    private final ExpensesService expensesService;

    ExpensesController(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @GetMapping("/{rawExpenseId}")
    ResponseEntity<ExpenseResponseDto> getSingleExpense(
            @PathVariable String rawExpenseId
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Expense> expenseById = expensesService.getExpenseById(new ExpenseId(rawExpenseId), userId);
        return ResponseEntity.of(expenseById.map(ExpenseResponseDto::fromDomain));
    }

    @GetMapping
    ResponseEntity<Page<ExpenseResponseDto>> getExpensesByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "expenseId") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(expensesService.findAllByPage(PageRequest.of(page, size, Sort.by(sortDirection, sortBy)), userId)
                .map(ExpenseResponseDto::fromDomain));
    }



    @PostMapping
    ResponseEntity<ExpenseResponseDto> registerNewExpense(
            @RequestBody @Valid RegisterExpenseRequest request
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        Expense newExpense = expensesService.registerNewExpense(request.title(), request.amount(), BudgetId.newOf(request.budgetId()), userId);
        ExpenseResponseDto expenseResponseDto = ExpenseResponseDto.fromDomain(newExpense);
        return ResponseEntity.created(URI.create("/expenses/" + expenseResponseDto.expenseId())).body(expenseResponseDto);
    }

    @DeleteMapping("/{rawExpenseId}")
    public ResponseEntity<ExpenseResponseDto> deleteExpense(@PathVariable String rawExpenseId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        expensesService.getExpenseById(new ExpenseId(rawExpenseId), userId)
                .ifPresent(expense -> expensesService.deleteExpenseById(expense.expenseId(), userId));
        return ResponseEntity.noContent().build();
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

    @PutMapping("/{rawExpenseId}")
    public ResponseEntity<ExpenseResponseDto> updateExpense(@PathVariable UUID rawExpenseId, @RequestBody RegisterExpenseRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Expense updatedExpense = expensesService.updateExpenseById(new ExpenseId(rawExpenseId.toString()), request.title(), request.amount(), userId);

        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(updatedExpense));
    }

    @PatchMapping("/{rawExpenseId}")
    public ResponseEntity<ExpenseResponseDto> updateExpenseField(@PathVariable UUID rawExpenseId,
                                                                 @RequestBody UpdateExpenseRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<BigDecimal> amount = (Optional.ofNullable(request.amount()));
        Optional<String> title = Optional.ofNullable(request.title());
        return ResponseEntity.of(expensesService.updateExpenseContent(new ExpenseId(rawExpenseId.toString()), title, amount, userId)
                .map(ExpenseResponseDto::fromDomain));
    }

    public static final String EXPENSES_BASE_PATH = "/expenses";
}
