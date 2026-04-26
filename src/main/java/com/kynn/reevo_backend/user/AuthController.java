package com.kynn.reevo_backend.user;   

import com.kynn.reevo_backend.user.api.UserFacade;
import com.kynn.reevo_backend.user.api.dto.AuthResponse;
import com.kynn.reevo_backend.user.api.dto.LoginRequest;
import com.kynn.reevo_backend.user.api.dto.RegisterRequest;
import com.kynn.reevo_backend.user.internal.domain.Account;
import com.kynn.reevo_backend.user.internal.service.JwtService;
import com.kynn.reevo_backend.user.internal.service.RefreshTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.kynn.reevo_backend.common.dto.ApiResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserFacade userFacade;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService; 

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(userFacade.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(userFacade.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(@RequestBody String refreshToken) {
        System.out.println("Received refresh token: " + refreshToken); // Debug log

        // Verify refresh token
        var rt = refreshTokenService.verifyRefreshToken(refreshToken);

        Account account = rt.getAccount();

        // Tạo access token mới
        String newAccessToken = jwtService.generateAccessToken(account);

        // Rotate refresh token (security tốt hơn)
        String newRefreshToken = refreshTokenService.createRefreshToken(account);

        return ApiResponse.ok(new AuthResponse(newAccessToken, newRefreshToken, account.getId()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody String refreshToken) {
        userFacade.logout(refreshToken);
        return ApiResponse.ok(null);
    }
}