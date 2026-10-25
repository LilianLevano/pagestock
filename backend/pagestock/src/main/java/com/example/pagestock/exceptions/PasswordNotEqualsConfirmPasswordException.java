package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid bij registratie wanneer het wachtwoord en de bevestiging ervan niet overeenkomen.
 * <p>
 * Geeft {@code 400 Bad Request} terug aan de client.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordNotEqualsConfirmPasswordException extends RuntimeException {
    /** Maakt de exception aan met een vaste foutmelding. */
    public PasswordNotEqualsConfirmPasswordException() {
        super("The passwords do not match.");
    }
}
