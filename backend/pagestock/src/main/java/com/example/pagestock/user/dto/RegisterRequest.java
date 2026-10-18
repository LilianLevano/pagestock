package com.example.pagestock.user.dto;

public record RegisterRequest(
        String firstname,
        String lastname,
        String email,
        String password,
        String confirmPassword,
        String phoneNumber) {
}
