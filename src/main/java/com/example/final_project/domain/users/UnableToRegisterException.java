package com.example.final_project.domain.users;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
class UnableToRegisterException extends IllegalStateException{
}
