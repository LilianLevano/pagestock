package com.example.pagestock.user.dto;

import jakarta.validation.constraints.NotBlank;
import org.aspectj.weaver.ast.Not;

/**
 * Request body voor het inloggen.
 *
 * @param email    e-mailadres van de gebruiker; mag niet leeg zijn. Hoofdletters en spaties
 *                 vooraan en achteraan maken niet uit.
 * @param password wachtwoord in platte tekst; mag niet leeg zijn
 */
public record LoginRequest(
        @NotBlank(message = "You must fill the email field to connect.")
        String email,

        @NotBlank(message = "You must fill the password field to connect.")
        String password
) {
}
