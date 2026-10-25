package com.example.pagestock.user.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body voor het registreren van een nieuwe gebruiker. Een nieuwe gebruiker krijgt altijd
 * de rol {@code USER}.
 *
 * @param firstName       voornaam; mag niet leeg zijn
 * @param lastName        achternaam; mag niet leeg zijn
 * @param email           geldig e-mailadres; mag niet leeg zijn en nog niet in gebruik zijn.
 *                        Wordt in kleine letters opgeslagen.
 * @param password        wachtwoord van minstens 8 tekens
 * @param confirmPassword herhaling van het wachtwoord; moet gelijk zijn aan {@code password}
 * @param phoneNumber     telefoonnummer; mag niet leeg zijn
 */
public record RegisterRequest(
        @NotBlank(message = "First name can not be empty." )
        String firstName,

        @NotBlank(message = "Last name can not be empty." )
        String lastName,

        @NotBlank(message = "Email name can not be empty." )
        @Column(unique = true, nullable = false)
        @Email(message = "Email is not valid")
        String email,

        @NotBlank(message = "Password can not be empty.")
        @Size(min = 8)
        String password,

        @NotBlank(message = "Password can not be empty.")
        @Size(min = 8)
        String confirmPassword,

        @NotBlank(message = "Phone number can not be empty.")
        @Column(unique = true, nullable = false)
        String phoneNumber) {
}
