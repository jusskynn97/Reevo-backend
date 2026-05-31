package com.kynn.reevo_backend.video.api.dto;

import java.util.List;

public record VideoFeedResponse(
        List<VideoItemResponse> items,
        String nextCursor
) {}
