package com.example.pagestock.user.controller;

import com.example.pagestock.user.dto.LoginRequest;
import com.example.pagestock.user.dto.LoginResponse;
import com.example.pagestock.user.dto.RegisterRequest;
import com.example.pagestock.user.dto.UserResponse;
import com.example.pagestock.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * REST-controller voor registratie en login, bereikbaar onder {@code /auth}.
 * <p>
 * Alle endpoints zijn publiek (zie {@code SecurityConfig}): je hebt nog geen token nodig om je te
 * registreren of in te loggen. Bij validatiefouten in de request body volgt
 * {@code 400 Bad Request} met de foutmeldingen per veld.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    /**
     * Maakt de controller aan.
     *
     * @param userService service met de business logic voor gebruikers
     */
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Registreert een nieuwe gebruiker met de rol {@code USER}. De gebruiker is daarna nog niet
     * ingelogd: daarvoor dient {@link #login(LoginRequest)}.
     * <p>
     * Endpoint: {@code POST /auth/register} — publiek.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — ongeldige body (leeg veld, ongeldig e-mailadres, wachtwoord
     *       korter dan 8 tekens) of wachtwoord en bevestiging verschillen</li>
     *   <li>{@code 409 Conflict} — het e-mailadres of het telefoonnummer is al geregistreerd</li>
     * </ul>
     *
     * @param registerRequest gegevens van de nieuwe gebruiker
     * @return {@code 201 Created} met de aangemaakte gebruiker, zonder wachtwoord
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // 201 Created: er is een nieuwe resource (gebruiker) aangemaakt.
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(registerRequest));
    }

    /**
     * Logt een gebruiker in en geeft een JWT-token terug, dat 24 uur geldig is
     * ({@code jwt.expiration-ms}).
     * <p>
     * Endpoint: {@code POST /auth/login} — publiek.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — e-mailadres of wachtwoord is leeg</li>
     *   <li>{@code 401 Unauthorized} — onbekend e-mailadres of verkeerd wachtwoord</li>
     * </ul>
     *
     * @param loginRequest e-mailadres en wachtwoord
     * @return {@code 200 OK} met het JWT-token en de rol van de gebruiker
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }




}
