package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReservationEmptyException extends RuntimeException {
    public ReservationEmptyException(String message) {
        super(message);
    }
}
