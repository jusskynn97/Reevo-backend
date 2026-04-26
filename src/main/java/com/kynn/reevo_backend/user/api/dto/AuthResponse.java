package com.kynn.reevo_backend.user.api.dto;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UUID userId
) {}