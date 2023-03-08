package com.example.final_project.domain.expenses;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ResponseStatus(HttpStatus.CONFLICT)
public class ExpenseTooBigException extends RuntimeException {

    public ExpenseTooBigException(String message) {
        super(message);
    }

    public ExpenseTooBigException(String message, Throwable cause) {
        super(message, cause);
    }
}
