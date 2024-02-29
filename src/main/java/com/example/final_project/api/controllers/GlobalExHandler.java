package com.example.final_project.api.controllers;

import com.example.final_project.api.responses.ErrorDTO;
import com.example.final_project.domain.expenses.exceptions.ExpenseTooBigException;
import com.example.final_project.domain.users.appusers.exceptions.UnableToCreateException;
import com.fasterxml.jackson.core.JsonParseException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @ExceptionHandler(UnableToCreateException.class)
    public ResponseEntity<ErrorDTO> handleUnableToRegisterExceptions(UnableToCreateException ex) {
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

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorDTO> handleBadCredentialsExceptionExceptions(BadCredentialsException ex) {
        String properResponse = ex.getMessage();
        if (ex.getMessage().equals("Bad credentials")) properResponse = "Invalid login or password";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ErrorDTO.newOf(
                                     properResponse,
                                     HttpStatus.UNAUTHORIZED,
                                     LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                             ));
    }

    @ExceptionHandler({InvalidBearerTokenException.class})
    ResponseEntity<ErrorDTO> handleInvalidBearerTokenException(InvalidBearerTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ErrorDTO.newOf(
                                     "The token is expired, revoked, malformed, or invalid.",
                                     HttpStatus.UNAUTHORIZED,
                                     LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                             ));
    }

    @ExceptionHandler({SignatureException.class})
    ResponseEntity<ErrorDTO> handleSignatureExceptionException(SignatureException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ErrorDTO.newOf(
                                     "The token is expired, revoked, malformed, or invalid.",
                                     HttpStatus.UNAUTHORIZED,
                                     LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                             ));
    }

    @ExceptionHandler({JsonParseException.class})
    ResponseEntity<ErrorDTO> handleJsonParseExceptionException(JsonParseException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ErrorDTO.newOf(
                                     "The token is expired, revoked, malformed, or invalid.",
                                     HttpStatus.UNAUTHORIZED,
                                     LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                             ));
    }

    @ExceptionHandler({MalformedJwtException.class})
    ResponseEntity<ErrorDTO> handleMalformedJwtExceptionException(MalformedJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ErrorDTO.newOf(
                                     "The token is expired, revoked, malformed, or invalid.",
                                     HttpStatus.UNAUTHORIZED,
                                     LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                             ));
    }

    @ExceptionHandler({AccessDeniedException.class})
    ResponseEntity<ErrorDTO> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ErrorDTO.newOf("Access forbidden", HttpStatus.FORBIDDEN,
                                                  LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                             ));
    }

    @ExceptionHandler({InsufficientAuthenticationException.class})
    ResponseEntity<ErrorDTO> handleInsufficientAuthenticationExceptionException(InsufficientAuthenticationException ex
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ErrorDTO.newOf("Access forbidden", HttpStatus.FORBIDDEN,
                                                  LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                             ));
    }

    @ExceptionHandler(AccountStatusException.class)
    ResponseEntity<ErrorDTO> handleAccountStatusException(AccountStatusException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ErrorDTO.newOf("User account is abnormal.", HttpStatus.UNAUTHORIZED,
                                                  LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                             ));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorDTO> handleOtherException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ErrorDTO.newOf(
                                     "ex.getMessage()" + ex.getMessage() + ex.getClass(),
                                     HttpStatus.INTERNAL_SERVER_ERROR,
                                     LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                             ));
    }
}
