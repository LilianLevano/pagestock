package com.example.pagestock.user.dto;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phoneNumber
) {
}
