package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordNotEqualsConfirmPasswordException extends RuntimeException {
    public PasswordNotEqualsConfirmPasswordException() {
        super("The passwords do not match.");
    }
}
