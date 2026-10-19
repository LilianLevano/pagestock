package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ReservationNotReservedException extends RuntimeException {
    public ReservationNotReservedException(String message) {
        super(message);
    }
}
