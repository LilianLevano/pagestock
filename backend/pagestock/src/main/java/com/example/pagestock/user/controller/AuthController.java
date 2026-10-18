package com.example.pagestock.user.controller;

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

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // POST /auth/register
    // @RequestBody: de JSON uit de body wordt omgezet naar een RegisterRequest.
    // @Valid: de annotaties in RegisterRequest (@NotBlank, @Email, @Size) worden gecontroleerd
    // VOORDAT de methode loopt; bij een fout stuurt Spring meteen 400 terug.
    // De controller bevat geen logica: e-mail al gebruikt (409), wachtwoorden verschillend (400)
    // en het hashen gebeuren allemaal in UserService.
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // 201 Created: er is een nieuwe resource (gebruiker) aangemaakt.
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(registerRequest));
    }




}
