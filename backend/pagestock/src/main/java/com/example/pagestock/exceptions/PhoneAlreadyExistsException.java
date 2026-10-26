package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid bij registratie wanneer het telefoonnummer al aan een bestaand account
 * gekoppeld is.
 * <p>
 * Geeft {@code 409 Conflict} terug aan de client. Zonder deze controle zou de unieke
 * kolom {@code phone_number} in de database de registratie weigeren met een {@code 500}.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class PhoneAlreadyExistsException extends RuntimeException {
    /**
     * @param message foutmelding voor de client
     */
    public PhoneAlreadyExistsException(String message) {
        super(message);
    }
}
