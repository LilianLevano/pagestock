package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateBookInReservationException extends RuntimeException {
    public DuplicateBookInReservationException(String message) {
        super(message);
    }
}
