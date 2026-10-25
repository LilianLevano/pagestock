package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid wanneer er meer exemplaren van een boek worden gevraagd dan er op voorraad zijn,
 * bij het aanmaken van een reservatie of bij het verlagen van de voorraad.
 * <p>
 * Geeft {@code 409 Conflict} terug aan de client.
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class OutOfStockException extends RuntimeException {
    /**
     * @param isbnNumber ISBN-nummer van het boek met onvoldoende voorraad
     * @param stock      huidige voorraad van het boek
     */
    public OutOfStockException(String isbnNumber, Long stock) {
        super("You can't order that many of that book ( " + isbnNumber + " ). There are only: " + stock + " left.");
    }
}
