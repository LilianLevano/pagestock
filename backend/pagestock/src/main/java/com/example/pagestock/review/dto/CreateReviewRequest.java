package com.example.pagestock.review.dto;

import jakarta.validation.constraints.*;

/**
 * Request body voor het plaatsen van een review.
 * <p>
 * Het boek zit niet in de body maar in de URL, en de gebruiker wordt bepaald via het JWT-token.
 *
 * @param rating  score van 1 tot en met 5. Ontbreekt het veld in de JSON, dan is de waarde
 *                {@code 0} en faalt de validatie.
 * @param comment tekst van de review; mag niet leeg zijn en is maximaal 2000 tekens lang
 */
public record CreateReviewRequest(

        @NotNull (message = "A review must have a rating")
        @Min(value = 1, message = "A rating can not be less than 1")
        @Max(value = 5, message = "A rating can not be more than 5")
        int rating,

        @NotBlank(message = "A comment can not be empty")
        @Size(max = 2000)
        String comment
) {
}
