package com.example.pagestock.user.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "First name can not be empty." )
        String firstname,

        @NotBlank(message = "Last name can not be empty." )
        String lastname,

        @NotBlank(message = "Email name can not be empty." )
        @Column(unique = true, nullable = false)
        @Email(message = "Email is not valid")
        String email,

        @NotBlank(message = "Password can not be empty.")
        @Size(min = 8)
        String password,

        @NotBlank(message = "Password can not be empty.")
        @Size(min = 8)
        String confirmPassword,

        @NotBlank(message = "Phone number can not be empty.")
        @Column(unique = true, nullable = false)
        String phoneNumber) {
}
