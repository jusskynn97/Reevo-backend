package com.kynn.reevo_backend.feed.internal.client.dto;

import java.util.List;
import java.util.UUID;

public record RecoRankRequest(
        UUID userId,
        List<RecoCandidate> candidates
) {
}

