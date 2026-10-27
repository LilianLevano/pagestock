package com.example.pagestock.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Een filter in de keten van Spring Security. OncePerRequestFilter garandeert
// dat hij maar één keer per request loopt, VOOR de controller.
// Zijn enige taak: "staat er een geldig token in de request? Zo ja, zeg aan Spring wie dit is."
/**
 * Filter die bij elke request het JWT-token uit de header {@code Authorization: Bearer <token>}
 * leest en, als het geldig is, de gebruiker in de {@link SecurityContextHolder} zet.
 * <p>
 * De filter weigert zelf nooit een request: zonder of met een ongeldig token blijft de request
 * anoniem, en beslissen de regels in {@code SecurityConfig} of dat mag ({@code 401} op een
 * beveiligde route).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Maakt de filter aan.
     *
     * @param jwtService         service om het token te controleren en het e-mailadres eruit te lezen
     * @param userDetailsService service om de gebruiker en zijn rol uit de database te laden
     */
    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Controleert het token van de request en registreert de gebruiker bij Spring Security.
     * <p>
     * De gebruiker wordt bij elke request opnieuw uit de database geladen, zodat een gewijzigde
     * rol meteen geldt en een verwijderde gebruiker niet meer binnenkomt.
     *
     * @param request     de binnenkomende request
     * @param response    de response
     * @param filterChain de rest van de filterketen; wordt altijd verder aangeroepen
     * @throws ServletException bij een fout verder in de filterketen
     * @throws IOException      bij een I/O-fout verder in de filterketen
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // De client stuurt het token in de header: "Authorization: Bearer eyJ..."
        String header = request.getHeader("Authorization");

        // Geen token (bv. GET /books of /auth/login): we doen niets en laten de request door.
        // De regels in SecurityConfig beslissen daarna of de route publiek is of niet.
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer" is 7 tekens: de rest is het token zelf.
        String token = header.substring(7);

        try {
            // Controleert de handtekening en de vervaldatum, en leest het e-mailadres.
            String email = jwtService.extractEmail(token);

            // Alleen als nog niemand ingelogd is voor deze request.
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(token, userDetails)) {
                    // We maken een "ingelogde gebruiker" voor Spring: wie (userDetails),
                    // geen wachtwoord nodig (null, het token is het bewijs) en zijn rollen.
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Vanaf hier weet Spring wie de gebruiker is, tot het einde van deze request.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtException | UsernameNotFoundException e) {
            // Vervalst, verlopen of onbekend token: we zetten niemand in de context.
            // De request blijft anoniem en een beveiligde route geeft dan 401.
        }

        // Altijd doorgeven aan de volgende filter in de keten.
        filterChain.doFilter(request, response);
    }
}