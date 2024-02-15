package com.example.final_project.domain.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class WrongCredentialsException extends IllegalStateException{
    public WrongCredentialsException(String message){
        super(message);
    }
}
