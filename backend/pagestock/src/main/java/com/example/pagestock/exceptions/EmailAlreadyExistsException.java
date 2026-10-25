package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid bij registratie wanneer het e-mailadres al aan een bestaand account gekoppeld is.
 * <p>
 * Geeft {@code 409 Conflict} terug aan de client.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {
    /** Maakt de exception aan met een vaste foutmelding. */
    public EmailAlreadyExistsException() {
        super("That email is already registered");
    }
}
