package com.example.pagestock.user.model;

/**
 * Rol van een {@link User}, die bepaalt welke endpoints hij of zij mag gebruiken.
 * <p>
 * Spring Security zet de rol om naar de autoriteit {@code ROLE_<naam>}
 * (bv. {@code ROLE_ADMIN}), waarop {@code hasRole("ADMIN")} in {@code SecurityConfig} controleert.
 */
public enum Role {
    /** Gewone klant: kan boeken reserveren, eigen reservaties annuleren en reviews schrijven. */
    USER,
    /**
     * Beheerder: beheert de catalogus, ziet alle openstaande reservaties en bevestigt ophalingen.
     * Mag ook elke reservatie annuleren en elke review verwijderen.
     */
    ADMIN
}
