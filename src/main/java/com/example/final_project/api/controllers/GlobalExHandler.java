package com.example.final_project.api.controllers;

import com.example.final_project.api.responses.ErrorDTO;
import com.example.final_project.domain.expenses.ExpenseTooBigException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDTO.newOf(
                ex
                        .getBindingResult()
                        .getAllErrors()
                        .stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.joining(
                                " , ")),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
                             .format(DateTimeFormatter.ISO_DATE_TIME)
        ));
    }

    @ExceptionHandler(ExpenseTooBigException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(ExpenseTooBigException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorDTO.newOf(
                ex
                        .getMessage(),
                HttpStatus.CONFLICT,
                LocalDateTime.now()
                             .format(DateTimeFormatter.ISO_DATE_TIME)
        ));
    }
}
