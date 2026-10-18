package com.example.pagestock.user.service;

import com.example.pagestock.exceptions.EmailAlreadyExistsException;
import com.example.pagestock.exceptions.PasswordNotEqualsConfirmPasswordException;
import com.example.pagestock.security.JwtService;
import com.example.pagestock.user.dto.LoginRequest;
import com.example.pagestock.user.dto.LoginResponse;
import com.example.pagestock.user.dto.RegisterRequest;
import com.example.pagestock.user.dto.UserResponse;
import com.example.pagestock.user.model.User;
import com.example.pagestock.user.repository.UserRepository;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,  AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    public UserResponse register(RegisterRequest registerRequest) {

        if(!registerRequest.password().equals(registerRequest.confirmPassword())) {
            throw new PasswordNotEqualsConfirmPasswordException();
        }

        String cleanEmail = registerRequest.email().trim().toLowerCase();

        if (userRepository.existsByEmail(cleanEmail)) {
            throw new EmailAlreadyExistsException();
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

    public LoginResponse login(LoginRequest loginRequest) {
        // Spring roept loadUserByUsername op en vergelijkt het wachtwoord met BCrypt.
        // Fout e-mailadres of wachtwoord: BadCredentialsException, Spring stuurt 401 terug.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email().trim().toLowerCase(),
                        loginRequest.password()));

        // Bij succes bevat de "principal" de UserDetails uit CustomUserDetailsService.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // We maken een token voor deze gebruiker en sturen het terug.
        return new LoginResponse(jwtService.generateToken(userDetails));
    }



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
