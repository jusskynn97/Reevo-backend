package com.kynn.reevo_backend.user.api;

import java.util.UUID;

import org.springframework.modulith.NamedInterface;

import com.kynn.reevo_backend.user.api.dto.AuthResponse;
import com.kynn.reevo_backend.user.api.dto.LoginRequest;
import com.kynn.reevo_backend.user.api.dto.RegisterRequest;
import com.kynn.reevo_backend.user.api.dto.UserSummary;

@NamedInterface
public interface UserFacade {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserSummary getUserSummary(UUID userId);

    void logout(String refreshToken);   // revoke refresh token
}