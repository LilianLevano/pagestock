package com.example.pagestock.user.dto;

import com.example.pagestock.user.model.Role;

public record LoginResponse(String token, String role) {
}
