package com.kynn.reevo_backend.user.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank 
        String email,
        @NotBlank 
        String password
) {}