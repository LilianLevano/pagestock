package com.example.pagestock.config;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration: deze klasse bevat geen businesslogica,
// ze maakt objecten ("beans") aan die Spring beheert.
// @EnableWebSecurity: we nemen de beveiligingsconfiguratie over
// in plaats van de standaardconfiguratie ("alles is geblokkeerd").
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Bean 1: het hulpmiddel dat wachtwoorden hasht.
    // Spring roept deze methode één keer op bij het opstarten en bewaart het resultaat.
    // Elke klasse die een PasswordEncoder in haar constructor vraagt
    // (bv. UserService) krijgt dit object van Spring. Dat is IoC.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean 2: de filterketen, dus de regels die elke request moet
    // doorlopen VOORDAT hij bij een controller aankomt.
    // Spring geeft ons een HttpSecurity-object (een "regelbouwer"),
    // wij configureren het, en build() maakt de uiteindelijke keten.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF beschermt sites die op een sessiecookie steunen.
                // Onze API gebruikt een token in een header, geen cookie:
                // de bescherming is hier nutteloos, en aan laten blokkeert alle POST-requests (403).
                .csrf(csrf -> csrf.disable())

                // STATELESS: de server bewaart geen sessie in het geheugen.
                // Elke request moet zelf bewijzen wie hij is
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // De toegangsregels, van boven naar onder gelezen: de EERSTE regel
                // die bij de request past, wordt toegepast, de rest wordt genegeerd.
                .authorizeHttpRequests(auth -> auth
                        // Registreren en inloggen: moeten publiek zijn,
                        // anders kan niemand ooit inloggen.
                        .requestMatchers("/auth/**").permitAll()
                        // De catalogus bekijken: publiek, maar alleen met GET.
                        // Een POST /books past niet bij deze regel en zakt verder door.
                        // "/**" = deze route en alles erachter (bv. /books/978...).
                        .requestMatchers(HttpMethod.GET, "/books/**", "/authors/**", "/categories/**").permitAll()
                        // Spring Boot stuurt bij een fout intern door naar /error om het antwoord op te bouwen.
                        // Zonder deze regel wordt elke fout (400, 404, 409) verborgen achter een 401.
                        .requestMatchers("/error").permitAll()

                        // Al de rest: je moet ingelogd zijn. Altijd als laatste.
                        .anyRequest().authenticated())

                // Tijdelijk: aanvaardt de header "Authorization: Basic ..." (email:wachtwoord).
                // Om met Postman te testen vóór de JWT, en het is ons plan B.
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // De AuthenticationManager controleert e-mail + wachtwoord.
    // Spring bouwt hem zelf met onze CustomUserDetailsService en onze PasswordEncoder;
    // wij maken hem alleen beschikbaar als bean, zodat UserService hem kan gebruiken.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



}