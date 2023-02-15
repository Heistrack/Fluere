package com.example.final_project.api;

import com.example.final_project.api.requests.RegisterExpenseRequest;
import com.example.final_project.api.responses.ExpenseResponseDto;
import com.example.final_project.domain.Expense;
import com.example.final_project.domain.ExpenseId;
import com.example.final_project.domain.ExpensesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.example.final_project.api.ExpensesController.EXPENSES_BASE_PATH;

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

    @PostMapping
    ResponseEntity<ExpenseResponseDto> registerNewExpense(
            @RequestBody @Valid RegisterExpenseRequest request
    ) {
        Expense newExpense = expensesService.registerNewExpense(request.title(), request.amount());
        ExpenseResponseDto expenseResponseDto = ExpenseResponseDto.fromDomain(newExpense);
        return ResponseEntity.created(URI.create("/expenses/" + expenseResponseDto.expenseId())).body(expenseResponseDto);
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

    static final String EXPENSES_BASE_PATH = "/expenses";
}
