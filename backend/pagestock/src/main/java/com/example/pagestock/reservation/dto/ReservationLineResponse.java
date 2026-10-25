package com.example.pagestock.reservation.dto;

/**
 * Response met één lijn van een reservatie.
 *
 * @param bookIsbn  ISBN-nummer van het gereserveerde boek
 * @param quantity  aantal gereserveerde exemplaren
 * @param bookTitle titel van het boek zoals die was op het moment van reserveren; wordt niet
 *                  aangepast als de titel van het boek later wijzigt
 */
public record ReservationLineResponse(
        String bookIsbn,
        int quantity,
        String bookTitle

) {
}
