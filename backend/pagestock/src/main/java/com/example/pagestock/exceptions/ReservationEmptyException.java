package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid wanneer een reservatie wordt aangemaakt zonder boeken.
 * <p>
 * Geeft {@code 409 Conflict} terug aan de client.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ReservationEmptyException extends RuntimeException {
    /**
     * @param message foutmelding voor de client
     */
    public ReservationEmptyException(String message) {
        super(message);
    }
}
