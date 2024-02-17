package com.example.final_project.api.controllers;

import com.example.final_project.api.responses.ErrorDTO;
import com.example.final_project.domain.expenses.exceptions.ExpenseTooBigException;
import com.example.final_project.domain.users.exceptions.UnableToRegisterException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDTO.newOf(
                ex.getBindingResult()
                  .getAllErrors()
                  .stream()
                  .map(ObjectError::getDefaultMessage)
                  .collect(Collectors.joining(
                          " , ")),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        ));
    }

    @ExceptionHandler(ExpenseTooBigException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(ExpenseTooBigException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorDTO.newOf(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        ));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorDTO> handleNoElementExceptions(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ErrorDTO.newOf(
                                     ex.getMessage(),
                                     HttpStatus.NOT_FOUND,
                                     LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                             ));
    }

    @ExceptionHandler(UnableToRegisterException.class)
    public ResponseEntity<ErrorDTO> handleUnableToRegisterExceptions(UnableToRegisterException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ErrorDTO.newOf(
                                     ex.getMessage(),
                                     HttpStatus.CONFLICT,
                                     LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                             ));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorDTO> handleJwtExceptions(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ErrorDTO.newOf(
                                     ex.getMessage(),
                                     HttpStatus.UNAUTHORIZED,
                                     LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                             ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDTO> handleBadCredentialsExceptionExceptions(BadCredentialsException ex) {
        String properResponse = ex.getMessage();
        if(ex.getMessage().equals("Bad credentials")) properResponse = "Invalid login or password";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ErrorDTO.newOf(
                                     properResponse,
                                     HttpStatus.UNAUTHORIZED,
                                     LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                             ));
    }
}
