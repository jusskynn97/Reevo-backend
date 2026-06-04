package com.kynn.reevo_backend.user.api.dto;

import java.util.UUID;

public record UserSummary(
        UUID id,
        String username,
        String displayName,
        String avatarUrl,
        String bio,
        long followerCount,
        long followingCount,
        long friendCount,
        boolean isFollowing,
        boolean isFriend
) {}