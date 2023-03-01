package com.example.final_project.api.controllers;

import com.example.final_project.api.requests.expenses.RegisterExpenseRequest;
import com.example.final_project.api.requests.expenses.UpdateExpenseRequest;
import com.example.final_project.api.responses.ExpenseResponseDto;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseId;
import com.example.final_project.domain.expenses.ExpensesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

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
        Optional<Expense> expenseById = expensesService.getExpenseById(new ExpenseId(rawExpenseId));
        return ResponseEntity.of(expenseById.map(ExpenseResponseDto::fromDomain));
    }

    @GetMapping
    ResponseEntity<Page<ExpenseResponseDto>> getExpensesByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "25") Integer size,
            @RequestParam(required = false, defaultValue = "expenseId") String sortBy
    ) {
        return ResponseEntity.ok(expensesService.findAllByPage((PageRequest.of(page, size, Sort.by(sortBy).descending())))
                .map(ExpenseResponseDto::fromDomain));
    }
    @PostMapping
    ResponseEntity<ExpenseResponseDto> registerNewExpense(
            @RequestBody @Valid RegisterExpenseRequest request
    ) {
        Expense newExpense = expensesService.registerNewExpense(request.title(), request.amount());
        ExpenseResponseDto expenseResponseDto = ExpenseResponseDto.fromDomain(newExpense);
        return ResponseEntity.created(URI.create("/expenses/" + expenseResponseDto.expenseId())).body(expenseResponseDto);
    }

    @DeleteMapping("/{rawExpenseId}")
    public ResponseEntity<ExpenseResponseDto> deleteExpense(@PathVariable String rawExpenseId) {
        expensesService.getExpenseById(new ExpenseId(rawExpenseId))
                .ifPresent(expense -> expensesService.deleteExpenseById(expense.expenseId()));
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
        Expense updatedExpense = expensesService.updateExpenseById(new ExpenseId(rawExpenseId.toString()), request.title(), request.amount());

        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(updatedExpense));
    }

    @PatchMapping("/{rawExpenseId}")
    public ResponseEntity<ExpenseResponseDto> updateExpenseField(@PathVariable UUID rawExpenseId,
                                                                 @RequestBody UpdateExpenseRequest request) {
        Optional<BigDecimal> amount = (Optional.ofNullable(request.amount()));
        Optional<String> title = Optional.ofNullable(request.title());
        return ResponseEntity.of(expensesService.updateExpenseContent(new ExpenseId(rawExpenseId.toString()), title, amount)
                .map(ExpenseResponseDto::fromDomain));
    }

    public static final String EXPENSES_BASE_PATH = "/expenses";
}
