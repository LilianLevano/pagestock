package com.example.pagestock.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Deze klasse doet maar twee dingen: een token MAKEN (bij login)
// en een token CONTROLEREN (bij elke volgende request).
/**
 * Maakt en controleert JWT-tokens, ondertekend met HMAC-SHA256.
 * <p>
 * Het token bevat als subject het e-mailadres van de gebruiker, plus het tijdstip van aanmaak
 * en de vervaldatum. De geheime sleutel ({@code jwt.secret}) en de geldigheidsduur
 * ({@code jwt.expiration-ms}) komen uit {@code application.properties}.
 */
@Service
public class JwtService {

    // De geheime sleutel waarmee we tokens ondertekenen. Alleen de server kent hem:
    // daarom kan een client geen geldig token vervalsen.
    private final SecretKey key;
    // Hoe lang een token geldig blijft, in milliseconden.
    private final long expirationMs;

    // @Value leest de waarden uit application.properties (jwt.secret, jwt.expiration-ms).
    /**
     * Maakt de service aan met de sleutel en de geldigheidsduur uit de configuratie.
     *
     * @param secret       geheime sleutel; minstens 32 tekens (256 bits), anders faalt het opstarten
     * @param expirationMs geldigheidsduur van een token in milliseconden
     */
    @Autowired
    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-ms}") long expirationMs) {
        // HMAC-SHA256 vraagt een sleutel van minstens 256 bits = 32 tekens.
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    // Wordt opgeroepen na een geslaagde login.
    /**
     * Maakt een nieuw ondertekend token voor een gebruiker.
     *
     * @param userDetails de ingelogde gebruiker; zijn gebruikersnaam (e-mailadres) wordt het subject
     * @return het token als string ({@code header.payload.handtekening})
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        return Jwts.builder()
                // "subject" = over wie gaat dit token: bij ons het e-mailadres.
                .subject(userDetails.getUsername())
                .issuedAt(now)
                // Na deze datum weigert de server het token (beperkt de schade bij diefstal).
                .expiration(new Date(now.getTime() + expirationMs))
                // De handtekening: berekend uit de inhoud + de geheime sleutel.
                .signWith(key)
                // Maakt de string "header.payload.handtekening".
                .compact();
    }

    // Leest het e-mailadres uit het token. parseSignedClaims controleert EERST
    // de handtekening en de vervaldatum: is het token vervalst of verlopen,
    // dan gooit het een exception en lezen we niets.
    /**
     * Leest het e-mailadres (subject) uit een token, na controle van handtekening en vervaldatum.
     *
     * @param token het JWT-token, zonder het voorvoegsel {@code "Bearer "}
     * @return het e-mailadres van de gebruiker
     * @throws io.jsonwebtoken.JwtException als het token vervalst, verlopen of ongeldig is
     */
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // Een token is geldig als het bij deze gebruiker hoort en niet verlopen is.
    /**
     * Controleert of een token bij de opgegeven gebruiker hoort en nog niet verlopen is.
     *
     * @param token       het JWT-token
     * @param userDetails de gebruiker die uit de database werd geladen
     * @return {@code true} als het subject overeenkomt met de gebruikersnaam en het token nog geldig is
     * @throws io.jsonwebtoken.JwtException als het token vervalst, verlopen of ongeldig is
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = parseClaims(token);
        return claims.getSubject().equals(userDetails.getUsername())
                && claims.getExpiration().after(new Date());
    }

    /**
     * Controleert de handtekening en de vervaldatum van een token en geeft de inhoud terug.
     *
     * @param token het JWT-token
     * @return de claims (inhoud) van het token
     * @throws io.jsonwebtoken.JwtException als het token vervalst, verlopen of ongeldig is
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}