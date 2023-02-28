package com.example.final_project.api;

import com.example.final_project.api.requests.RegisterExpenseRequest;
import com.example.final_project.api.requests.UpdateExpenseRequest;
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
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

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

    @GetMapping
    ResponseEntity<List<ExpenseResponseDto>> getAllExpenses(){

        List<Expense> expenses = expensesService.getExpenses();
        return ResponseEntity.ok(expenses.stream().map(ExpenseResponseDto::fromDomain).toList());
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
    public ResponseEntity<ExpenseResponseDto> updateExpense(@PathVariable UUID rawExpenseId, @RequestBody RegisterExpenseRequest request){
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

    static final String EXPENSES_BASE_PATH = "/expenses";
}
