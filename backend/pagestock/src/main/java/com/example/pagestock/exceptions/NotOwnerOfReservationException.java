package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotOwnerOfReservationException extends RuntimeException {
    public NotOwnerOfReservationException(String message) {
        super(message);
    }
}
