package com.example.final_project.api.controllers;

import com.example.final_project.api.requests.expenses.RegisterExpenseRequest;
import com.example.final_project.api.requests.expenses.UpdateExpenseRequest;
import com.example.final_project.api.responses.ErrorDTO;
import com.example.final_project.api.responses.ExpenseResponseDto;
import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.expenses.Expense;
import com.example.final_project.domain.expenses.ExpenseId;
import com.example.final_project.domain.expenses.ExpenseTooBigException;
import com.example.final_project.domain.expenses.ExpensesService;
import com.example.final_project.domain.users.UserContextProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        String userId = UserContextProvider.getUserContext().userId().value();
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
        String userId = UserContextProvider.getUserContext().userId().value();
        return ResponseEntity.ok(expensesService.findAllByPage(PageRequest.of(page, size, Sort.by(sortDirection, sortBy)), userId)
                .map(ExpenseResponseDto::fromDomain));
    }


    @PostMapping
    ResponseEntity<ExpenseResponseDto> registerNewExpense(
            @RequestBody @Valid RegisterExpenseRequest request
    ) {
        String userId = UserContextProvider.getUserContext().userId().value();
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        Expense newExpense = expensesService.registerNewExpense(request.title(), request.amount(), BudgetId.newOf(request.budgetId()), userId);
        ExpenseResponseDto expenseResponseDto = ExpenseResponseDto.fromDomain(newExpense);
        return ResponseEntity.created(URI.create("/expenses/" + expenseResponseDto.expenseId())).body(expenseResponseDto);
    }

    @DeleteMapping("/{rawExpenseId}")
    public ResponseEntity<ExpenseResponseDto> deleteExpense(@PathVariable String rawExpenseId) {
        String userId = UserContextProvider.getUserContext().userId().value();
        expensesService.getExpenseById(new ExpenseId(rawExpenseId), userId)
                .ifPresent(expense -> expensesService.deleteExpenseById(expense.expenseId(), userId));
        return ResponseEntity.noContent().build();
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDTO.newOf(ex
                        .getBindingResult()
                        .getAllErrors()
                        .stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.joining(" , ")),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ExpenseTooBigException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(ExpenseTooBigException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorDTO.newOf(ex
                        .getMessage(),
                HttpStatus.CONFLICT,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
    }

    @PutMapping("/{rawExpenseId}")
    public ResponseEntity<ExpenseResponseDto> updateExpense(@PathVariable UUID rawExpenseId, @RequestBody RegisterExpenseRequest request) {
        String userId = UserContextProvider.getUserContext().userId().value();
        Expense updatedExpense = expensesService.updateExpenseById(new ExpenseId(rawExpenseId.toString()), request.title(), request.amount(), userId);

        return ResponseEntity.ok(ExpenseResponseDto.fromDomain(updatedExpense));
    }

    @PatchMapping("/{rawExpenseId}")
    public ResponseEntity<ExpenseResponseDto> updateExpenseField(@PathVariable UUID rawExpenseId,
                                                                 @RequestBody UpdateExpenseRequest request) {
        String userId = UserContextProvider.getUserContext().userId().value();
        Optional<BigDecimal> amount = (Optional.ofNullable(request.amount()));
        Optional<String> title = Optional.ofNullable(request.title());
        System.out.println(rawExpenseId.toString());
        return ResponseEntity.of(expensesService.updateExpenseContent(new ExpenseId(rawExpenseId.toString()), title, amount, userId)
                .map(ExpenseResponseDto::fromDomain));
    }

    public static final String EXPENSES_BASE_PATH = "/expenses";
}
