package com.example.pagestock.review.dto;

import java.time.LocalDateTime;

/**
 * Response met de gegevens van een review.
 *
 * @param id               unieke identificatie van de review
 * @param rating           score van 1 tot en met 5
 * @param comment          tekst van de review
 * @param createdAt        tijdstip waarop de review werd geplaatst
 * @param authorReviewName volledige naam (voornaam en achternaam) van de gebruiker die de review
 *                         schreef; het e-mailadres wordt niet getoond
 */
public record ReviewResponse(
        Long id,
        int rating,
        String comment,
        LocalDateTime createdAt,
        String authorReviewName
) {
}
