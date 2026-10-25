package com.example.pagestock.user.dto;

import com.example.pagestock.user.model.Role;

/**
 * Response met de gegevens van een gebruiker. Het wachtwoord (ook de hash) wordt nooit
 * teruggestuurd.
 *
 * @param id          unieke identificatie van de gebruiker
 * @param firstName   voornaam
 * @param lastName    achternaam
 * @param email       e-mailadres
 * @param phoneNumber telefoonnummer
 * @param role        rol van de gebruiker
 */
public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Role role
) {
}
