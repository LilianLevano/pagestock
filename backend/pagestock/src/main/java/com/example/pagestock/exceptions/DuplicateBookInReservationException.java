package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid wanneer hetzelfde boek (ISBN-nummer) meer dan één keer voorkomt in een nieuwe
 * reservatie. Per boek hoort één lijn met de gewenste hoeveelheid.
 * <p>
 * Geeft {@code 400 Bad Request} terug aan de client.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateBookInReservationException extends RuntimeException {
    /**
     * @param message foutmelding voor de client
     */
    public DuplicateBookInReservationException(String message) {
        super(message);
    }
}
