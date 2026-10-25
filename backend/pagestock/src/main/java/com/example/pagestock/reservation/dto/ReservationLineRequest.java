package com.example.pagestock.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Eén lijn van een nieuwe reservatie: een boek en het gewenste aantal exemplaren.
 *
 * @param bookIsbn ISBN-nummer van een bestaand boek; mag niet leeg zijn en mag maar één keer
 *                 voorkomen in dezelfde reservatie
 * @param quantity aantal exemplaren; minstens 1. Ontbreekt het veld in de JSON, dan is de
 *                 waarde {@code 0} en faalt de validatie.
 */
public record ReservationLineRequest(
        @NotBlank(message = "Book isbn can not be empty.")
        String bookIsbn,

        @Min(value = 1, message = "You must at least order 1 book to place the order.")
        int quantity
) {
}
