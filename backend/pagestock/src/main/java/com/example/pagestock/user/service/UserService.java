package com.example.pagestock.user.service;

import com.example.pagestock.catalog.repository.BookRepository;
import com.example.pagestock.exceptions.EmailAlreadyExistsException;
import com.example.pagestock.exceptions.ItemNotFoundException;
import com.example.pagestock.exceptions.PasswordNotEqualsConfirmPasswordException;
import com.example.pagestock.exceptions.PhoneAlreadyExistsException;
import com.example.pagestock.security.JwtService;
import com.example.pagestock.user.dto.LoginRequest;
import com.example.pagestock.user.dto.LoginResponse;
import com.example.pagestock.user.dto.RegisterRequest;
import com.example.pagestock.user.dto.UserResponse;
import com.example.pagestock.user.model.User;
import com.example.pagestock.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Business logic voor gebruikers: registreren, inloggen en opzoeken.
 * <p>
 * Andere modules ({@code reservation}, {@code review}) halen gebruikers op via deze service,
 * niet rechtstreeks via {@link UserRepository}.
 */
@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BookRepository bookRepository;

    /**
     * Maakt de service aan met de benodigde afhankelijkheden.
     *
     * @param userRepository        repository voor gebruikers
     * @param passwordEncoder       BCrypt-encoder om wachtwoorden te hashen
     * @param authenticationManager controleert e-mailadres en wachtwoord bij het inloggen
     * @param jwtService            maakt het JWT-token aan na een geslaagde login
     * @param bookRepository        repository voor boeken (momenteel niet gebruikt in deze service)
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.bookRepository = bookRepository;
    }


    /**
     * Registreert een nieuwe gebruiker met de rol {@code USER}.
     * <p>
     * Het e-mailadres wordt in kleine letters en zonder spaties opgeslagen, voor- en achternaam
     * zonder spaties vooraan en achteraan, en het wachtwoord als BCrypt-hash.
     *
     * @param registerRequest gegevens van de nieuwe gebruiker
     * @return de aangemaakte gebruiker, zonder wachtwoord
     * @throws PasswordNotEqualsConfirmPasswordException als wachtwoord en bevestiging verschillen
     * @throws EmailAlreadyExistsException               als het e-mailadres al in gebruik is
     */
    public UserResponse register(RegisterRequest registerRequest) {

        if(!registerRequest.password().equals(registerRequest.confirmPassword())) {
            throw new PasswordNotEqualsConfirmPasswordException();
        }

        String cleanEmail = registerRequest.email().trim().toLowerCase();

        if (userRepository.existsByEmail(cleanEmail)) {
            throw new EmailAlreadyExistsException();
        }

        if(userRepository.existsByPhoneNumber(registerRequest.phoneNumber())) {
            throw new PhoneAlreadyExistsException("A user with the same phone number already exists.");
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.password());

        User user = new User();
            user.setFirstName(registerRequest.firstName().trim());
            user.setLastName(registerRequest.lastName().trim());
            user.setEmail(cleanEmail);
            user.setPassword(encodedPassword);
            user.setPhoneNumber(registerRequest.phoneNumber());

        return toResponse(userRepository.save(user));
    }

    /**
     * Logt een gebruiker in en geeft een JWT-token terug.
     * <p>
     * De controle van e-mailadres en wachtwoord gebeurt door de {@link AuthenticationManager},
     * die {@link CustomUserDetailService} gebruikt en het wachtwoord met BCrypt vergelijkt.
     *
     * @param loginRequest e-mailadres en wachtwoord
     * @return het JWT-token en de rol van de gebruiker
     * @throws org.springframework.security.authentication.BadCredentialsException als het
     *         e-mailadres onbekend is of het wachtwoord niet klopt (wordt {@code 401 Unauthorized})
     */
    public LoginResponse login(LoginRequest loginRequest) {
        // Spring roept loadUserByUsername op en vergelijkt het wachtwoord met BCrypt.
        // Fout e-mailadres of wachtwoord: BadCredentialsException, Spring stuurt 401 terug.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email().trim().toLowerCase(),
                        loginRequest.password()));

        // Bij succes bevat de "principal" de UserDetails uit CustomUserDetailsService.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Rol uit de authorities halen ("ROLE_ADMIN" -> "ADMIN")
        String role = userDetails.getAuthorities().iterator().next()
                .getAuthority().replace("ROLE_", "");

        // We maken een token voor deze gebruiker en sturen het terug.
        return new LoginResponse(jwtService.generateToken(userDetails), role);
    }

    /**
     * Zoekt een gebruiker op via zijn e-mailadres. Het e-mailadres wordt niet genormaliseerd:
     * geef het in kleine letters mee, zoals het uit het JWT-token komt.
     *
     * @param email e-mailadres van de gebruiker
     * @return de gebruiker, of {@link Optional#empty()} als die niet bestaat
     */
    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    /**
     * Zoekt een gebruiker op via zijn id.
     *
     * @param id id van de gebruiker
     * @return de gebruiker, of {@link Optional#empty()} als die niet bestaat
     */
    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }

    /**
     * Geeft de volledige naam van een gebruiker terug, bv. om bij een review te tonen.
     *
     * @param id id van de gebruiker
     * @return voornaam en achternaam, gescheiden door een spatie
     * @throws ItemNotFoundException als er geen gebruiker met dit id bestaat
     */
    public String getFullName(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("User not found"));

        return user.getFirstName() + " " + user.getLastName();
    }


    /**
     * Zet een {@link User}-entiteit om naar een {@link UserResponse}, zonder wachtwoord.
     *
     * @param user de om te zetten gebruiker
     * @return de response-representatie van de gebruiker
     */
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }

}
