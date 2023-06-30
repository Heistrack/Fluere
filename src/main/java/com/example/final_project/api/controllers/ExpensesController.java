package com.example.final_project.api.controllers;

import com.example.final_project.api.requests.expenses.RegisterExpenseRequest;
import com.example.final_project.api.requests.expenses.UpdateExpenseRequest;
import com.example.final_project.api.responses.ErrorDTO;
import com.example.final_project.api.responses.ExpenseResponseDto;
import com.example.final_project.domain.budgets.BudgetId;
import com.example.final_project.domain.expenses.*;
import com.example.final_project.domain.tokens.TokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
	public static final String EXPENSES_BASE_PATH = "/expenses";
	private final ExpensesService expensesService;
	private final TokenService tokenService;

	ExpensesController(ExpensesService expensesService, TokenService tokenService) {
		this.expensesService = expensesService;
		this.tokenService = tokenService;
	}

	@GetMapping("/{rawExpenseId}")
	ResponseEntity<ExpenseResponseDto> getSingleExpense(
			@PathVariable String rawExpenseId,
			Authentication authentication
	) {
		String userId = tokenService.extractUserIdFromToken(authentication);
		Optional<Expense> expenseById = expensesService.getExpenseById(new ExpenseId(rawExpenseId), userId);
		return ResponseEntity.of(expenseById.map(ExpenseResponseDto::fromDomain));
	}

	@GetMapping
	ResponseEntity<Page<ExpenseResponseDto>> getExpensesByPage(
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "25") Integer size,
			@RequestParam(required = false, defaultValue = "expenseId") String sortBy,
			@RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
			Authentication authentication
	) {
		String userId = tokenService.extractUserIdFromToken(authentication);
		return ResponseEntity.ok(expensesService.findAllByPage(
						PageRequest.of(page, size, Sort.by(sortDirection, sortBy)), userId)
				.map(ExpenseResponseDto::fromDomain));
	}

	@GetMapping("/budget/{rawBudgetId}")
	ResponseEntity<Page<ExpenseResponseDto>> getExpensesByBudgetId(
			@PathVariable String rawBudgetId,
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "25") Integer size,
			@RequestParam(required = false, defaultValue = "budgetId") String sortBy,
			@RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortDirection,
			Authentication authentication
	) {
		String userId = tokenService.extractUserIdFromToken(authentication);
		return ResponseEntity.ok(expensesService.findAllExpensesByBudgetId(userId,
						BudgetId.newOf(rawBudgetId),
						PageRequest.of(page, size, Sort.by(sortDirection, sortBy)))
				.map(ExpenseResponseDto::fromDomain));
	}

	@PostMapping
	ResponseEntity<ExpenseResponseDto> registerNewExpense(
			@RequestBody @Valid RegisterExpenseRequest request,
			Authentication authentication
	) {
		String userId = tokenService.extractUserIdFromToken(authentication);
		Expense newExpense = expensesService.registerNewExpense(request.title(), request.amount(),
				BudgetId.newOf(request.budgetId()), userId, request.typeOfExpense());
		ExpenseResponseDto expenseResponseDto = ExpenseResponseDto.fromDomain(newExpense);
		return ResponseEntity.created(URI.create("/expenses/" + expenseResponseDto.expenseId())).body(expenseResponseDto);
	}

	@DeleteMapping("/{rawExpenseId}")
	public ResponseEntity<ExpenseResponseDto> deleteExpense(
			@PathVariable String rawExpenseId,
			Authentication authentication) {
		String userId = tokenService.extractUserIdFromToken(authentication);
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
	public ResponseEntity<ExpenseResponseDto> updateExpense(
			@PathVariable UUID rawExpenseId,
			@RequestBody RegisterExpenseRequest request,
			Authentication authentication) {
		String userId = tokenService.extractUserIdFromToken(authentication);

		Expense updatedExpense = expensesService.updateExpenseById(
				ExpenseId.newId(rawExpenseId.toString()),
				BudgetId.newOf(request.budgetId()),
				request.title(),
				request.amount(),
				userId,
				request.typeOfExpense().orElse(TypeOfExpense.NO_CATEGORY));

		return ResponseEntity.ok(ExpenseResponseDto.fromDomain(updatedExpense));
	}

	@PatchMapping("/{rawExpenseId}")
	public ResponseEntity<ExpenseResponseDto> updateExpenseField(
			@PathVariable UUID rawExpenseId,
			@RequestBody UpdateExpenseRequest request,
			Authentication authentication) {
		String userId = tokenService.extractUserIdFromToken(authentication);

		Optional<BigDecimal> amount = (Optional.ofNullable(request.amount()));
		Optional<String> title = Optional.ofNullable(request.title());

		return ResponseEntity.of(expensesService.updateExpenseContent(
						new ExpenseId(rawExpenseId.toString()), title, amount, userId, request.typeOfExpense())
				.map(ExpenseResponseDto::fromDomain));
	}
}
