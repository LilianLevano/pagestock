package com.example.pagestock.exceptions;

public class NotOwnerOfReservationException extends RuntimeException {
    public NotOwnerOfReservationException(String message) {
        super(message);
    }
}
