package com.example.pagestock.user.service;

import com.example.pagestock.user.model.User;
import com.example.pagestock.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Laadt gebruikers voor Spring Security op basis van hun e-mailadres.
 * <p>
 * Wordt gebruikt door de {@code AuthenticationManager} bij het inloggen en door de
 * {@code JwtAuthenticationFilter} bij elke request met een JWT-token.
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Maakt de service aan.
     *
     * @param userRepository repository om gebruikers op te halen
     */
    @Autowired
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Zoekt een gebruiker op via zijn e-mailadres en zet hem om naar een Spring Security
     * {@link UserDetails}, met de BCrypt-hash van het wachtwoord en de rol als autoriteit
     * ({@code ROLE_USER} of {@code ROLE_ADMIN}).
     *
     * @param email e-mailadres van de gebruiker; hoofdletters en spaties vooraan en achteraan
     *              maken niet uit
     * @return de gegevens van de gebruiker in het formaat van Spring Security
     * @throws UsernameNotFoundException als er geen gebruiker met dit e-mailadres bestaat
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Zelfde normalisatie als bij register, anders vindt "Test@mail.com" niemand.
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                // Onbekend e-mailadres: Spring zet deze exception om naar 401.
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // We zetten onze entity om naar een UserDetails, het formaat dat Spring begrijpt.
        // Volledige naam nodig: onze eigen klasse heet ook User (zelfde naam, ander package).
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                // De BCrypt-hash uit de database. Spring vergelijkt die ZELF met het
                // getypte wachtwoord via passwordEncoder.matches(...): wij doen dat niet.
                .password(user.getPassword())
                // roles("USER") wordt de autoriteit "ROLE_USER".
                // Dinsdag controleert hasRole("ADMIN") op "ROLE_ADMIN".
                .roles(user.getRole().name())
                .build();
    }
}
