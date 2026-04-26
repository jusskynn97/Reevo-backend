package com.kynn.reevo_backend.user.internal.service;

import com.kynn.reevo_backend.user.api.UserFacade;
import com.kynn.reevo_backend.user.api.dto.AuthResponse;
import com.kynn.reevo_backend.user.api.dto.LoginRequest;
import com.kynn.reevo_backend.user.api.dto.RegisterRequest;
import com.kynn.reevo_backend.user.api.dto.UserSummary;
import com.kynn.reevo_backend.user.event.UserRegisteredEvent;
import com.kynn.reevo_backend.user.internal.domain.Account;
import com.kynn.reevo_backend.user.internal.domain.AccountStatus;
import com.kynn.reevo_backend.user.internal.domain.UserProfile;
import com.kynn.reevo_backend.user.internal.repository.AccountRepository;
import com.kynn.reevo_backend.user.internal.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements UserFacade {

    private final AccountRepository accountRepo;
    private final UserProfileRepository profileRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public AuthResponse register(RegisterRequest req) {
        if (accountRepo.existsByEmail(req.email())) {
            throw new BadCredentialsException("Email already in use");
        }
        if (accountRepo.existsByUsername(req.username())) {
            throw new BadCredentialsException("Username already in use");
        }

        Account account = new Account();
        account.setUsername(req.username());
        account.setEmail(req.email());
        account.setPasswordHash(passwordEncoder.encode(req.password()));
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());

        account = accountRepo.save(account);

        // Tạo UserProfile mặc định
        UserProfile profile = new UserProfile();
        profile.setAccount(account);
        profile.setDisplayName(req.displayName() != null && !req.displayName().isBlank() 
                ? req.displayName() : req.username());
        profile.setWalletBalance(java.math.BigDecimal.ZERO);
        profileRepo.save(profile);

        // Publish event (các module khác có thể lắng nghe để gửi email xác thực, tạo wallet, v.v.)
        eventPublisher.publishEvent(new UserRegisteredEvent(this, account, profile.getDisplayName()));

        String accessToken = jwtService.generateAccessToken(account);
        String refreshToken = refreshTokenService.createRefreshToken(account);

        return new AuthResponse(accessToken, refreshToken, account.getId());
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        Account account = accountRepo.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(req.password(), account.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BadCredentialsException("Account is " + account.getStatus());
        }

        account.setLastLoginAt(LocalDateTime.now());
        accountRepo.save(account);

        String accessToken = jwtService.generateAccessToken(account);
        String refreshToken = refreshTokenService.createRefreshToken(account);

        return new AuthResponse(accessToken, refreshToken, account.getId());
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }

    @Override
    public UserSummary getUserSummary(UUID userId) {
        UserProfile profile = profileRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        return new UserSummary(
                profile.getAccount().getId(),
                profile.getAccount().getUsername(),
                profile.getDisplayName(),
                profile.getAvatarUrl()
        );
    }
}