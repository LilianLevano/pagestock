package com.example.pagestock.user.dto;

import com.example.pagestock.user.model.Role;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Role role
) {
}
