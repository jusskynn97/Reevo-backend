package com.kynn.reevo_backend.user.internal.service;

import com.kynn.reevo_backend.user.internal.domain.Account;
import com.kynn.reevo_backend.user.internal.domain.RefreshToken;
import com.kynn.reevo_backend.user.internal.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh.expiration.days:7}")
    private int refreshExpirationDays;

    public String createRefreshToken(Account account) {
        // Xóa refresh token cũ của user (rotation strategy đơn giản)
        refreshTokenRepository.deleteByAccountId(account.getId());

        String token = jwtService.generateRefreshToken(account);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAccount(account);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(refreshExpirationDays));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token is expired or revoked");
        }

        return refreshToken;
    }

    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                });
    }

    public void revokeAllTokensForUser(UUID accountId) {
        refreshTokenRepository.deleteByAccountId(accountId);
    }
}