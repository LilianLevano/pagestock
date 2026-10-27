package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid wanneer een reservatie wordt opgehaald of geannuleerd terwijl haar status niet
 * meer {@code RESERVED} is (bv. al opgehaald of al geannuleerd).
 * <p>
 * Geeft {@code 409 Conflict} terug aan de client.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ReservationNotReservedException extends RuntimeException {
    /**
     * @param message foutmelding voor de client, met de huidige status van de reservatie
     */
    public ReservationNotReservedException(String message) {
        super(message);
    }
}
