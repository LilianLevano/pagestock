package com.example.pagestock.user.dto;

import com.example.pagestock.user.model.Role;

/**
 * Response na een geslaagde login.
 *
 * @param token JWT-token dat de client bij volgende requests meestuurt in de header
 *              {@code Authorization: Bearer <token>}
 * @param role  rol van de gebruiker zonder prefix ({@code "USER"} of {@code "ADMIN"}), zodat de
 *              frontend weet welke schermen hij moet tonen
 */
public record LoginResponse(String token, String role) {
}
