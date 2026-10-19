package com.example.pagestock.config;

import com.example.pagestock.security.JwtAuthenticationFilter;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    // De filter wordt door Spring geïnjecteerd als parameter van deze @Bean-methode.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/books/**", "/authors/**", "/categories/**").permitAll()
                        // Alle ANDERE methodes (POST, PUT, DELETE) op de catalogus: alleen ADMIN.
                        // hasRole("ADMIN") zoekt naar de autoriteit "ROLE_ADMIN".
                        .requestMatchers("/books/**", "/authors/**", "/categories/**", "/reservations/*/pickup").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/reservations").hasRole("ADMIN")
                        .anyRequest().authenticated())

                // Zonder httpBasic geeft Spring standaard 403 als er geen token is.
                // We willen 401 ("wie ben je?") en geen 403 ("ik weet wie je bent, maar het mag niet").
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                // Onze JWT-filter loopt VOOR de standaardfilter die met gebruikersnaam/wachtwoord werkt,
                // zodat de gebruiker al gekend is wanneer de toegangsregels gecontroleerd worden.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

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