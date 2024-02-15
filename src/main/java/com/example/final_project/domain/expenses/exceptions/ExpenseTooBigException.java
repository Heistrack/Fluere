package com.example.final_project.domain.expenses.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
public class ExpenseTooBigException extends RuntimeException {
    public ExpenseTooBigException(String message) {
        super(message);
    }
}
