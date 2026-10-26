package com.example.pagestock.reservation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request body voor het aanmaken van een nieuwe reservatie.
 * <p>
 * De gebruiker zit niet in de body: die wordt bepaald via het JWT-token.
 *
 * @param lines de boeken die worden gereserveerd, één lijn per boek; minstens één lijn verplicht.
 *              Elke lijn wordt ook gevalideerd ({@code @Valid}).
 */
public record CreateReservationRequest(
        @NotEmpty(message = "A reservation must have at least one line.")
        @Valid
        List<ReservationLineRequest> lines
) {
}
