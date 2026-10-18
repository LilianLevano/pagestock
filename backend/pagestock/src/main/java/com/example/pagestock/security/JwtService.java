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
@Service
public class JwtService {

    // De geheime sleutel waarmee we tokens ondertekenen. Alleen de server kent hem:
    // daarom kan een client geen geldig token vervalsen.
    private final SecretKey key;
    // Hoe lang een token geldig blijft, in milliseconden.
    private final long expirationMs;

    // @Value leest de waarden uit application.properties (jwt.secret, jwt.expiration-ms).
    @Autowired
    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-ms}") long expirationMs) {
        // HMAC-SHA256 vraagt een sleutel van minstens 256 bits = 32 tekens.
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    // Wordt opgeroepen na een geslaagde login.
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
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // Een token is geldig als het bij deze gebruiker hoort en niet verlopen is.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = parseClaims(token);
        return claims.getSubject().equals(userDetails.getUsername())
                && claims.getExpiration().after(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}