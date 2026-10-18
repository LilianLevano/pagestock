package com.example.pagestock.user.dto;

import jakarta.validation.constraints.NotBlank;
import org.aspectj.weaver.ast.Not;

public record LoginRequest(
        @NotBlank(message = "You must fill the email field to connect.")
        String email,

        @NotBlank(message = "You must fill the password field to connect.")
        String password
) {
}
