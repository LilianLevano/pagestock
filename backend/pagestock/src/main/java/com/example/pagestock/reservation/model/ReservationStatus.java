package com.example.pagestock.reservation.model;

/**
 * Mogelijke statussen van een {@link Reservation}.
 * <p>
 * Een reservatie begint als {@link #RESERVED} en gaat daarna naar {@link #PICKED_UP} of
 * {@link #CANCELLED}. Beide eindstatussen zijn definitief.
 */
public enum ReservationStatus {
    /** Aangemaakt en wacht op ophaling; kan nog worden opgehaald of geannuleerd. */
    RESERVED,
    /** Opgehaald in de winkel; de voorraad van de boeken is verlaagd. */
    PICKED_UP,
    /** Geannuleerd door de eigenaar of door een {@code ADMIN}; de voorraad is niet gewijzigd. */
    CANCELLED
}
