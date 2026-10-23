package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ReviewDeleteNotAllowedException extends RuntimeException {
    public ReviewDeleteNotAllowedException(String message) {
        super(message);
    }
}
