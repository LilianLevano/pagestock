package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid wanneer een gebruiker een reservatie wil annuleren die niet van hem of haar is.
 * <p>
 * Geeft {@code 403 Forbidden} terug aan de client.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotOwnerOfReservationException extends RuntimeException {
    /**
     * @param message foutmelding voor de client
     */
    public NotOwnerOfReservationException(String message) {
        super(message);
    }
}
