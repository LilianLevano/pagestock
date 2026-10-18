package com.example.pagestock.user.service;

import com.example.pagestock.user.model.User;
import com.example.pagestock.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security kent onze tabel "users" niet. Via deze interface leggen we uit
// HOE het een gebruiker moet laden. Spring roept deze klasse zelf op bij elke login
// (nu bij elke Basic-request, dinsdag bij POST /auth/login).
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // "username" is de naam die Spring gebruikt, bij ons is dat het e-mailadres.
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
