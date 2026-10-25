package com.example.pagestock.reservation.dto;

import com.example.pagestock.reservation.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response met de gegevens van een reservatie en haar lijnen.
 *
 * @param id         unieke identificatie van de reservatie
 * @param status     huidige status: {@code RESERVED}, {@code PICKED_UP} of {@code CANCELLED}
 * @param reservedAt tijdstip waarop de reservatie werd aangemaakt
 * @param pickedUpAt tijdstip waarop de reservatie werd opgehaald; {@code null} zolang ze niet
 *                   is opgehaald
 * @param lines      de gereserveerde boeken met hun aantallen
 */
public record ReservationResponse(
        Long id,
        ReservationStatus status,
        LocalDateTime reservedAt,
        LocalDateTime pickedUpAt,
        List<ReservationLineResponse> lines
) {
}
