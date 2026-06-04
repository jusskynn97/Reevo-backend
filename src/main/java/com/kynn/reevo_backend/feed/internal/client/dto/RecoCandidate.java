package com.kynn.reevo_backend.feed.internal.client.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecoCandidate(
        UUID videoId,
        UUID uploaderId,
        LocalDateTime uploadedAt,
        boolean isFollowing,
        int likeCount,
        int commentCount,
        long durationSeconds,
        double tagPreferenceScore
) {
}
