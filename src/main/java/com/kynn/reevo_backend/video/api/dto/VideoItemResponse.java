package com.kynn.reevo_backend.video.api.dto;

import java.util.UUID;

public record VideoItemResponse(
        UUID id,
        String videoUrl,
        String thumbnailUrl,
        String description,
        String uploaderName,
        String uploaderAvatar,
        UUID uploaderId,
        Long duration,
        String uploadedAt,
        int likeCount,
        int commentCount,
        boolean isLiked
) {}