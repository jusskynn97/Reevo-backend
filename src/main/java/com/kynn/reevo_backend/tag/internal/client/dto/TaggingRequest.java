package com.kynn.reevo_backend.tag.internal.client.dto;

import java.util.UUID;

public record TaggingRequest(
        UUID videoId,
        String videoUrl,
        String thumbnailUrl,
        String description
) {
}

