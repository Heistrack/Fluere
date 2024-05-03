package com.example.final_project.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UnableToCreateException extends IllegalStateException {
    public UnableToCreateException(String message) {
        super(message);
    }
}
