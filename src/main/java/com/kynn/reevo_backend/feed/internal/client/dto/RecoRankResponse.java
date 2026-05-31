package com.kynn.reevo_backend.feed.internal.client.dto;

import java.util.List;
import java.util.UUID;

public record RecoRankResponse(List<UUID> rankedVideoIds) {
}

